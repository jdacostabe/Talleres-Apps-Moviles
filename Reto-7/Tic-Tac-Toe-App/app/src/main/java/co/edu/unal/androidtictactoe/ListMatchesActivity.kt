package co.edu.unal.androidtictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JsPromptResult
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONObject
import org.json.JSONException




class ListMatchesActivity : AppCompatActivity() {

    lateinit var listView:ListView
    private lateinit var mSocket: Socket

    var listData = ArrayList<String>()
    var idsData = ArrayList<Int>()

    lateinit var listAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_matches)
        listView = findViewById(R.id.matches_list)

        listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listData)
        listView.adapter = listAdapter
        listView.setOnItemClickListener { parent, view, position, id -> sendJoinRequest(position)}

        // Connect the Android app to the server.
        SocketHandler.setSocket()
        SocketHandler.establishConnection()
        mSocket = SocketHandler.getSocket()

        mSocket.emit("get-matches", "", Ack {

            listData.clear()
            idsData.clear()

            try{
                val data:JSONObject = (it[0] as JSONObject).get("matches") as JSONObject
                val iter: Iterator<String> = data.keys()

                while (iter.hasNext()) {
                    val key = iter.next()
                    idsData.add(key.toInt())
                    try {
                        val value: Any = data.get(key)
                        listData.add("Match Number: $key  |  Players: $value")
                    } catch (e: JSONException) { }
                }
            }catch (e:JSONException){ }

            runOnUiThread { listAdapter?.notifyDataSetChanged() }

        })

        mSocket.on("update-matches"){ args ->

            listData.clear()
            idsData.clear()

            try{
                val data:JSONObject = (args[0] as JSONObject).get("matches") as JSONObject
                val iter: Iterator<String> = data.keys()

                while (iter.hasNext()) {
                    val key = iter.next()
                    idsData.add(key.toInt())
                    try {
                        val value: Any = data.get(key)
                        listData.add("Match Number: $key  |  Players: $value")
                    } catch (e: JSONException) { }
                }
            }catch (e:JSONException){ }

            runOnUiThread { listAdapter?.notifyDataSetChanged() }

        }

    }

    fun sendJoinRequest(id:Int){
        var payload:String = "{id:${idsData[id]}}"
        mSocket.emit("join-match", JSONObject(payload), Ack {
            var data:JSONObject = it[0] as JSONObject
            if(data.has("error")){
                SocketHandler.closeConnection()
                finish()
            }else{
                val Join_Game = Intent(Intent(this@ListMatchesActivity, MultiplayerActivity::class.java))
                startActivity(Join_Game)
            }
        })

    }
}