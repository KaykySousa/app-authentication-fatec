package com.example.appauthentication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appauthentication.ui.theme.AppAuthenticationTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppAuthenticationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun App(modifier: Modifier = Modifier) {

    val auth = Firebase.auth

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var currentUser by remember {
        mutableStateOf(auth.currentUser)
    }

    LaunchedEffect(Unit) {
        auth.addAuthStateListener {
            currentUser = auth.currentUser
        }
    }

    fun handleRegister() {
        if (email.isBlank() || password.isBlank()) return

        auth.createUserWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("App", "User created successfully")
                email = ""
                password = ""
            } else {
                Log.e("App", "User creation failed: ${task.exception}")
            }
        }
    }

    fun handleSignIn() {
        if (email.isBlank() || password.isBlank()) return

        auth.signInWithEmailAndPassword(
            email,
            password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i("App", "User signed in successfully")
                email = ""
                password = ""
            } else {
                Log.e("App", "User sign in failed: ${task.exception}")
            }
        }
    }

    fun handleSignOut() {
        auth.signOut()
        Log.i("App", "Current User: $currentUser")
    }


    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (currentUser == null) {
            Text("Faça login para acessar o aplicativo")

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("E-mail") }
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") }
            )

            Button(
                onClick = { handleSignIn() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Button(
                onClick = { handleRegister() },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Registrar")
            }

        } else {
            Text("Bem-vindo, ${currentUser?.email}")

            Button(
                onClick = {
                    handleSignOut()
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text("Sair")
            }
        }

    }
}