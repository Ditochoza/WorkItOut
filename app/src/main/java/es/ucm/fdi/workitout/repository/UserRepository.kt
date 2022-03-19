package es.ucm.fdi.workitout.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import es.ucm.fdi.workitout.model.User
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val dbUsers = FirebaseFirestore.getInstance().collection(DbConstants.COLLECTION_USERS)

    suspend fun register(name: String, email: String, password: String) {
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            dbUsers.document(email).set(User(name = name)).await()
        } catch(e: Exception) {
            //TODO Mensaje error
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            val result = auth.signInWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            //TODO Mensaje error
        }
    }

}