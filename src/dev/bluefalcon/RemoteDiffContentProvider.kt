import { TextDocumentContentProvider, EventEmitter, Uri, scm,  commands, window, SourceControl, Event, ViewColumn } from "vscode";
import { ProjectModel, RemoteDocumentModel, RemoteDiffModel, Placeholder } from "@gitliveapp/core";
import { captureError } from "../initSentry";
import { RSA_NO_PADDING } from "node:constants";


export class RemoteDiffContentProvider implements TextDocumentContentProvider {

    private readonly models = new Map<string, RemoteDiffModel>();

    private readonly onDidChangeEmitter = new EventEmitter<Uri>();

    constructor(project: ProjectModel<Uri>) {
        // listen to the any new RemoteDiffModel being sent through
        // an openRemoteDiffModel is sent through whenever a file node is being clicked by the user
        // the action that sends a RemoteDiffModel through is registered
        project.openRemoteDiff.collect(async model => {

            const uri = Uri.parse(`gitlive-diff://file/${model.title}?${model.afterPath || model.beforePath}`);

            this.models.set(uri.query, model);

            // create a new uri with the fragement part replaced by "base"
            const base = uri.with({ fragment: 'base' });

            model.placeholder.collect((placeholder: Placeholder)=>{
            // tell core you opened a webview to signal no need to load anymore
            project.openDocuments = {['webview']: Uri.parse('')}
            // open the webview
            const panel = window.createWebviewPanel(
                    'getRevision', // Identifies the type of the webview. Used internally
            'Missing working copy', // Title of the panel displayed to the user
            ViewColumn.One, // Editor column to show the new webview panel in.
            {enableScripts: true} // Webview options. More on these later.
            );

            panel.webview.html = getWebviewContent(placeholder);
            panel.webview.onDidReceiveMessage(
                    async () => {
                await commands.executeCommand('workbench.action.newWindow');
                //close the webview
                panel.dispose()
                return;
            }
            );
        })

            // the fire method which is used to notify VS Code when a change has happened in a document
            // The provider will then be called again to provide the updated content, assuming the document is still open
            model.beforeContent.collect(() => this.onDidChangeEmitter.fire(base));
            model.afterContent.collect(() => this.onDidChangeEmitter.fire(uri));
            // when remoteDiffModel dies this funciton runs
            model.finally(() => this.models.delete(uri.query));

            // wait for succesfull revision retrieval bfore attempting to open diff
            await model.beforeContent.first()
            // Opens the provided resources in the diff editor to compare their contents
            // the gitlive-dii scheme has been registered meaning on each side of the vs code fidd window
            // the returned value of provideTextDocumentContent below will be shown
            await commands.executeCommand('vscode.diff', base, uri, model.title)
        });

    }

    onDidChange = this.onDidChangeEmitter.event;

    // whenever the gitlive-diff scheme gets called within the open document logic the RemoteDiffContentProvider 
    // which got registered under that scheme provides its content defined in the function below
    provideTextDocumentContent(uri: Uri) {
        try {
            // get the remoteDiffModel of the passed in query
            const model = this.models.get(uri.query)!;
            // get the content flow
            const content = uri.fragment === 'base' ? model.beforeContent : model.afterContent;
            // extract the first emitted value and of the content flow and return it
            return content.first();
        } catch(e) {
            captureError(e);
            throw e;
        }
    }
}


function getWebviewContent(placeholder: Placeholder) {
    return `<!DOCTYPE html>
    <html lang="en">
    <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Clone repository to view teammates diff</title>
    </head>
    <body>
    <h1> ${placeholder.title}</h1>
    <button onclick=handleButton()>Open Repository in new workspace</button>
    <script>
            const vscode = acquireVsCodeApi();
    function handleButton() {
        vscode.postMessage({
            text: 'openRevision'
        })
    }
    </script>
    </body>
    </html>`;
}
