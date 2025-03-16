package com.arthurgichuhi.kotlinopengl.core.xmlParser

import android.content.Context
import android.util.Log
import com.arthurgichuhi.kotlinopengl.utils.Utils
import java.io.BufferedReader
import java.util.regex.Pattern

class XmlParser {

    companion object{
        private val DATA = Pattern.compile(">(.+?)<")
        private val START_TAG = Pattern.compile("<(.+?)>")
        private val ATTR_NAME = Pattern.compile("(.+?)=")
        private val ATTR_VAL = Pattern.compile("\"(.+?)\"")
        private val CLOSED = Pattern.compile("(</|/>)")

        private val utils = Utils()
        private val attributes : MutableMap<String,String> = HashMap()
        private val children: MutableMap<String,MutableList<XmlNode>> = HashMap()

        fun readXMLFile(ctx:Context,name:String):XmlNode?{
            val reader = utils.readXMLFile(ctx,name)
            val node = loadNode(reader)
            node?.childNodes=children
            node?.attributes=attributes
            reader.close()
            return node
        }

        private fun loadNode(reader:BufferedReader):XmlNode?{
            try{
                val line = reader.readLine()?.trim() ?: return null
                if(line.startsWith("</")){
                    return null
                }
                val startTagParts = getStartTag(line).split(" ")
                val node = XmlNode(startTagParts[0].replace("/",""))
                addAttributes(startTagParts,node)
                addData(line,node)
                //Log.d("TAG","Attributes Size${node.myName}:${attributes.size}:${children.size}")
                attributes.putAll(node.attributes)
                if(CLOSED.matcher(line).find()){
                    children.putAll(node.childNodes)
                    return node
                }
                var child: XmlNode?
                while (loadNode(reader).also { child = it } != null){
                    node.addChild(child!!)
                    children.putAll(node.childNodes)
                }
                return node
            }
            catch(e:Error){

                return null
            }
        }

        fun addData(line:String,node: XmlNode){
            val matcher = DATA.matcher(line)
            if(matcher.find()){
                node.setDataValue(matcher.group(1)!!)
            }
        }

        fun addAttributes(titleParts:List<String>,node: XmlNode){
            for(i in titleParts.indices){
                if(titleParts[i].contains("=")){
                    addAttribute(titleParts[i],node)
                }
            }
        }

        fun addAttribute(attrLine:String,node: XmlNode){
            val nameMatch = ATTR_NAME.matcher(attrLine)
            nameMatch.find()
            val valMatch = ATTR_VAL.matcher(attrLine)
            valMatch.find()
            if(node.attributes[nameMatch.group(1)!!]!=null){
                node.attributes[nameMatch.group(1)!!] = "${node.attributes[nameMatch.group(1)!!]}${valMatch.group(1)!!}"
            }
            else{
                node.attributes[nameMatch.group(1)!!] = valMatch.group(1)!!
            }

        }

        fun getStartTag(line:String):String{
            val match = START_TAG.matcher(line)
            match.find()
            return match.group(1)!!
        }
    }
}