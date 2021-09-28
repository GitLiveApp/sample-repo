package dev.gitlive.internal.view
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.internal.InitializedFlow
import dev.gitlive.internal.model.Delta
import dev.gitlive.internal.model.Project
import dev.gitlive.internal.model.toGutterIndicator
import dev.gitlive.internal.protocol.CurrentFile
import dev.gitlive.internal.protocol.User
import dev.gitlive.internal.protocol.Host
import dev.gitlive.internal.protocol.User
import dev.gitlive.internal.protocol.WorkingCopy
import dev.gitlive.internal.protocol.TextEdit
import dev.gitlive.model.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient
import kotlin.test.*

Pushed
Pushed 
Pushed 
