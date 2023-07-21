package com.example.notesapp.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import androidx.activity.viewModels
import com.example.notesapp.R
import com.example.notesapp.databinding.ActivityLoginregisterBinding
import com.example.notesapp.model.User
import com.example.notesapp.util.UiState
import com.example.notesapp.util.isValidEmail
import com.example.notesapp.util.showShortToast
import com.example.notesapp.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginRegisterActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding:ActivityLoginregisterBinding
    private var check:Boolean = true
    val viewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginregisterBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnLogin.setOnClickListener(this)
        binding.tvLoginPage.setOnClickListener(this)

        observer()
    }

    override fun onStart() {
        super.onStart()
        viewModel.getSession { user ->
            if (user != null){
                startActivity(Intent(this, NotesActivity::class.java))
                finish()
            }
        }
    }

    private fun observer() {
        viewModel.register.observe(this) { state ->
            when(state){
                is UiState.Loading -> {
                    showProgress()
                    binding.loginButton.visibility = GONE
                }
                is UiState.Failure -> {
                    hideProgress()
                    binding.loginButton.visibility = VISIBLE
                    showShortToast(this,state.error.toString())
                }
                is UiState.Success -> {
                    hideProgress()
                    binding.loginButton.visibility = VISIBLE
                    showShortToast(this,state.data)
                    startActivity(Intent(this, NotesActivity::class.java))
                    finish()
                }
            }
        }

        viewModel.login.observe(this) { state ->
            when(state){
                is UiState.Loading -> {
                    showProgress()
                    binding.loginButton.visibility = GONE
                }
                is UiState.Failure -> {
                    hideProgress()
                    binding.loginButton.visibility = VISIBLE
                    showShortToast(this,state.error.toString())
                }
                is UiState.Success -> {
                    hideProgress()
                    binding.loginButton.visibility = VISIBLE
                    showShortToast(this,state.data)
                    startActivity(Intent(this, NotesActivity::class.java))
                    finish()
                }
            }
        }
    }


    override fun onClick(view: View?) {
        if (view != null) {
            when(view.id){
                R.id.btnLogin->{
                    validateData()
                }
                R.id.tvLoginPage->{
                    switchPage()
                }
            }
        }
    }

    private fun validateData() {
        if (check){
            if (binding.etEmail.text.toString().isEmpty()){
                showShortToast(this,"Email is empty.")
            }else if (binding.etPassword.text.toString().isEmpty()){
                showShortToast(this,"Password is empty.")
            }else{
                viewModel.login(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString()
                )
            }
        }else{
            if (binding.etName.text.toString().isEmpty()){
                showShortToast(this,"Name is empty.")
            }else if (binding.etEmail.text.toString().isEmpty()){
                showShortToast(this,"Email is empty.")
            }else if (!binding.etEmail.text.toString().isValidEmail()){
                showShortToast(this,"Invalid email.")
            }else if (binding.etPassword.text.toString().isEmpty()){
                showShortToast(this,"Password is empty.")
            }else{
                viewModel.register(
                    email = binding.etEmail.text.toString(),
                    password = binding.etPassword.text.toString(),
                    user = getUserObj()
                )
            }
        }
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            name = binding.etName.text.toString(),
            email = binding.etEmail.text.toString(),
        )
    }


    private fun switchPage() {
        if (check){
            binding.loginButton.text = getString(R.string.register)
            check = false
            binding.tvLoginPage.text = getString(R.string.click_to_login)
            binding.etName.visibility = VISIBLE
        }else{
            binding.loginButton.text = getString(R.string.login)
            binding.tvLoginPage.text = getString(R.string.click_to_register)
            binding.etName.visibility = GONE
            check = true
        }
    }


    private fun showProgress(){
        binding.progressLoading.visibility = VISIBLE
    }

    private fun hideProgress(){
        binding.progressLoading.visibility = GONE
    }
}