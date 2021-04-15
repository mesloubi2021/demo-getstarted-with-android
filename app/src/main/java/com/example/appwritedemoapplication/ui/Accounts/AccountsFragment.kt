package com.example.appwritedemoapplication.ui.Accounts

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.appwritedemoapplication.R
import com.example.appwritedemoapplication.databinding.FragmentAccountBinding


class AccountsFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var viewModel: AccountsViewModel

    override fun onCreateView(
        inflater: LayoutInflater ,
        container: ViewGroup? ,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(AccountsViewModel::class.java)
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_account,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.login.setOnClickListener{
            viewModel.onLogin(binding.email.text, binding.password.text)
        }

        binding.signup.setOnClickListener{
            viewModel.onSignup(binding.email.text, binding.password.text, binding.name.text)
        }

        viewModel.error.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                Toast.makeText(requireContext(), it.message , Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.response.observe(viewLifecycleOwner, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                binding.responseTV.setText(it)
            }
        })

        return binding.root
    }
}