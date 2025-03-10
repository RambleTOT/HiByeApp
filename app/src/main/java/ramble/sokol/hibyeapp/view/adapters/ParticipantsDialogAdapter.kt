package ramble.sokol.hibyeapp.view.adapters

import android.graphics.drawable.Drawable
import android.media.Image
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import ramble.sokol.hibyeapp.R

class ParticipantsDialogAdapter(
    private val userNames: List<String>,
    private val userDescriptions: List<String>,
    private val photoLinkArr: List<String>
) : RecyclerView.Adapter<ParticipantsDialogAdapter.ParticipantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParticipantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_participant_all_dialog, parent, false)
        return ParticipantViewHolder(view)
    }

    override fun onBindViewHolder(holder: ParticipantViewHolder, position: Int) {
        holder.bind(userNames[position], userDescriptions[position], photoLinkArr[position])
    }

    override fun getItemCount(): Int {
        return userNames.size
    }

    class ParticipantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val participantName: TextView = itemView.findViewById(R.id.participant_name)
        private val participantDescription: TextView = itemView.findViewById(R.id.participant_description)
        private val imageParticipant: ImageView = itemView.findViewById(R.id.image_participant_dialog)

        fun bind(name: String, description: String, photoLink: String) {
            participantName.text = name
            participantDescription.text = description

            if (!photoLink.isEmpty()) {

                Glide.with(imageParticipant.context)
                    .load(photoLink)
                    .apply(RequestOptions.bitmapTransform(CircleCrop()))
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            imageParticipant.setImageResource(R.drawable.icon_profile)
                            return true
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(imageParticipant)
            } else {
                imageParticipant.setImageResource(R.drawable.icon_profile)
            }
        }
    }
}