package com.datatree.core.chat

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.datatree.core.interfaces.OpenAIService
import com.datatree.databinding.BottomSheetChatBinding
import com.datatree.infraestructure.adapters.ChatAdapter
import com.datatree.infraestructure.dataclass.DataMessage
import com.datatree.infraestructure.dataclass.visordataclasses.ContentItem
import com.datatree.infraestructure.dataclass.visordataclasses.Message
import com.datatree.infraestructure.dataclass.visordataclasses.OpenAIRequest
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.hypot

class BottomSheetChatDialog : BottomSheetDialogFragment() {

    private var chatAdapter: ChatAdapter? = null
    private val messageList: MutableList<DataMessage> = mutableListOf()

    private var _binding: BottomSheetChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adapter con mensajes iniciales
        loadSampleMessages()
        chatAdapter = ChatAdapter(messageList)

        // Configurar RecyclerView
        binding.recyclerViewMessages.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMessages.adapter = chatAdapter

        // Botón de enviar
        binding.buttonSend.setOnClickListener {
            val text = binding.TextInputLayout.editText?.text?.toString()?.trim()
            if (!text.isNullOrEmpty()) {
                // Enviar mensaje del usuario
                val message = DataMessage(text, true,"https://tse1.mm.bing.net/th/id/OIP.rE9RwmI4B7-kIJBuKKMAsgHaD9?rs=1&pid=ImgDetMain&o=7&rm=3" ,System.currentTimeMillis())
                messageList.add(message)
                chatAdapter?.notifyItemInserted(messageList.size - 1)
                binding.recyclerViewMessages.smoothScrollToPosition(messageList.size - 1)

                //binding.TextInputLayout.clearFocus()
                binding.TextInputLayout.editText?.setText("")

                //Llamar a la api de OpenAI en esta parte.

                botReply(text)
            }
        }

        // Animación de entrada circular
        binding.circularLayoutChat.post {
            val centerX = binding.circularLayoutChat.width / 2
            val centerY = binding.circularLayoutChat.height / 2
            val finalRadius = hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

            binding.circularLayoutChat.visibility = View.VISIBLE

            val anim = ViewAnimationUtils.createCircularReveal(
                binding.circularLayoutChat,
                centerX,
                centerY,
                0f,
                finalRadius
            )
            anim.duration = 400
            anim.start()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)


            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        //obtenemos el comportamiento del bottom sheet.


        //val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        val behavior = BottomSheetBehavior.from(bottomSheet!!)

        behavior.state = BottomSheetBehavior.PEEK_HEIGHT_AUTO

        behavior.isFitToContents = true

        //behavior.peekHeight = 30
    }

    private fun botReply(userMessage: String) {
        // Mostrar indicador de "procesando"
        val loadingMessage = DataMessage("Analizando...", false, "", System.currentTimeMillis())
        messageList.add(loadingMessage)
        chatAdapter?.notifyItemInserted(messageList.size - 1)
        binding.recyclerViewMessages.smoothScrollToPosition(messageList.size - 1)

        // Coroutine para llamar a la API
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = getBotReplyFromOpenAI(userMessage)

                // Remover mensaje de "analizando"
                messageList.remove(loadingMessage)
                chatAdapter?.notifyDataSetChanged()

                // Agregar respuesta de la IA
                val botMessage = DataMessage(response, false, "", System.currentTimeMillis())
                messageList.add(botMessage)
                chatAdapter?.notifyItemInserted(messageList.size - 1)
                binding.recyclerViewMessages.smoothScrollToPosition(messageList.size - 1)
            } catch (e: Exception) {
                messageList.remove(loadingMessage)
                chatAdapter?.notifyDataSetChanged()

                val errorMessage = DataMessage(
                    "Ocurrió un error al obtener respuesta del asistente: ${e.message}",
                    false, "", System.currentTimeMillis()
                )
                messageList.add(errorMessage)
                chatAdapter?.notifyItemInserted(messageList.size - 1)
            }
        }
    }

    // Función para llamar a OpenAI
    private suspend fun getBotReplyFromOpenAI(userMessage: String): String {

        val openAIApiKey = "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"

        val content = mutableListOf<ContentItem>()
        content.add(ContentItem(type = "text", text = """
        Eres un asistente experto en cultivos y análisis de datos agrícolas.
        Responde de manera clara, con sugerencias prácticas para mejorar la salud de las plantas
        y analiza datos agrícolas avanzados según el mensaje del usuario.
        Mensaje del usuario: "$userMessage"
    """.trimIndent()))

        val message = Message(role = "user", content = content)
        val request = OpenAIRequest(
            model = "gpt-4-turbo",
            messages = listOf(message),
            max_tokens = 300
        )

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val openAIService = retrofit.create(OpenAIService::class.java)

        val response = withContext(Dispatchers.IO) {
            openAIService.getCompletion("Bearer $openAIApiKey", request)
        }

        return response.choices[0].message.content
    }


    override fun dismiss() {
        val centerX = binding.circularLayoutChat.width / 2
        val centerY = binding.circularLayoutChat.height / 2
        val initialRadius = hypot(centerX.toDouble(), centerY.toDouble()).toFloat()

        val anim = ViewAnimationUtils.createCircularReveal(
            binding.circularLayoutChat,
            centerX,
            centerY,
            initialRadius,
            0f
        )
        anim.duration = 400
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super@BottomSheetChatDialog.dismissAllowingStateLoss()
            }
        })
        anim.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadSampleMessages() {
        messageList.add(DataMessage("Estoy aquí para ayudarte a que tus plantas crezcan sanas y fuertes. ¿Empezamos? 🛡️", false,"https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200&icon_names=cruelty_free", System.currentTimeMillis()))
        //messageList.add(DataMessage("", true,"", System.currentTimeMillis()))
    }

    private fun botReply_(reply: String) {
        binding.recyclerViewMessages.postDelayed({
            val botMessage = DataMessage("Gracias por su interés, continuamos en proceso de desarrollo. Att: Flora", false,"", System.currentTimeMillis())
            messageList.add(botMessage)
            chatAdapter?.notifyItemInserted(messageList.size - 1)
            binding.recyclerViewMessages.smoothScrollToPosition(messageList.size - 1)
        }, 400) // retraso para simular respuesta
    }
}
