package com.example.tiendadecelulares.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tiendadecelulares.R
import com.example.tiendadecelulares.network.User

class AdminUserAdapter(
    private var userList: List<User>,
    private val onBlockClicked: (User) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.blockButton.setOnClickListener { onBlockClicked(user) }
    }

    override fun getItemCount(): Int = userList.size

    fun setData(newUserList: List<User>) {
        userList = newUserList
        notifyDataSetChanged()
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.user_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.user_email)
        val blockButton: Button = itemView.findViewById(R.id.block_user_button)

        fun bind(user: User) {
            // Mostrar nombre si existe, sino mostrar email como título
            val displayName =  user.EMAIL
            nameTextView.text = displayName
            emailTextView.text = user.EMAIL

            // Si tu backend tiene isBlocked, alternar texto; si no, show "Bloquear"
            //val blocked = user.isBlocked ?: false
            //blockButton.text = if (blocked) "Desbloquear" else "Bloquear"

            // (Opcional) puedes cambiar color o estilo del botón según blocked
        }
    }
}
