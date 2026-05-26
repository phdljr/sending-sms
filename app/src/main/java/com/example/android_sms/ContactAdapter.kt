package com.example.android_sms

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val list: List<Contact>
) : RecyclerView.Adapter<ContactAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val nameText: TextView = view.findViewById(R.id.nameText)
        val container: View = view  // 전체 아이템 클릭용
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.nameText.text = "${item.name} (${item.phone})"

        // 기존 리스너 제거 (RecyclerView 버그 방지 필수)
        holder.checkBox.setOnCheckedChangeListener(null)

        // 상태 동기화
        holder.checkBox.isChecked = item.isSelected

        // 체크박스 클릭
        holder.checkBox.setOnCheckedChangeListener { _, checked ->
            item.isSelected = checked
        }

        // 아이템 전체 클릭 -> 토글
        holder.container.setOnClickListener {
            item.isSelected = !item.isSelected
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = list.size
}
