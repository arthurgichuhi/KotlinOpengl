package com.arthurgichuhi.kotlinopengl.core.xmlParser

import android.util.Log

class XmlNode(private val name:String) {
    val myName = name
    var data = ""
    var attributes : MutableMap<String,String> = HashMap()
    var childNodes: MutableMap<String,MutableList<XmlNode>> = HashMap()
    var line = ""

        /**
     * Gets the value of a certain attribute of the node. Returns {@code null}
     * if the attribute doesn't exist.
     *
     * @param attr
     *            - the name of the attribute.
     * @return The value of the attribute.
     */
    fun getAttribute(attr:String):String?{
        return attributes[attr]
    }

    /**
     * Gets a certain child node of this node.
     *
     * @param childName
     *            - the name of the child node.
     * @return The child XML node with the given name.
     */

    fun getChild(childName:String):XmlNode?{
        val nodes = childNodes[childName]
        return nodes?.get(0)
    }
    /**
     * Gets a child node with a certain name, and with a given value of a given
     * attribute. Used to get a specific child when there are multiple child
     * nodes with the same node name.
     *
     * @param childName
     *            - the name of the child node.
     * @param attr
     *            - the attribute whose value is to be checked.
     * @param value
     *            - the value that the attribute must have.
     * @return The child node which has the correct name and the correct value
     *         for the chosen attribute.
     */
    fun getChildWithAttribute(childName: String,attr: String,value:String):XmlNode?{
        val children = getChildren(childName)
        if(children.isEmpty())return null
        for(child in children){
//            Log.d("TAG","$value Child------${child.myName}")
            val tmp = child.getAttribute(attr)
            if(tmp==value){
                return child
                }
        }
        return null
    }

    /**
     * Get the child nodes of this node that have a given name.
     *
     * @param name
     *            - the name of the child nodes.
     * @return A list of the child nodes with the given name. If none exist then
     *         an empty list is returned.
     */
    fun getChildren(name: String):List<XmlNode>{
        return childNodes[name]?.toList()?:ArrayList()
    }
    /**
     * Adds a new attribute to this node. An attribute has a name and a value.
     * Attributes are stored in a HashMap which is initialized in here if it was
     * previously null.
     *
     * @param attr
     *            - the name of the attribute.
     * @param value
     *            - the value of the attribute.
     */
    fun addAttribute(attr:String,value:String){
        if(attributes[attr]!=null){
            Log.d("TAG","Attributes $attr exist")
        }
        attributes[attr] = value
    }

    /**
     * Adds a child node to this node.
     *
     * @param child
     *            - the child node to add.
     */
    fun addChild(child:XmlNode){
        if(childNodes[child.myName]==null){
            childNodes[child.myName]= listOf(child).toMutableList()
        }
        else{
            val prevChildren = childNodes[child.myName] as MutableList<XmlNode>
            prevChildren.add(child)
            childNodes[child.myName] = prevChildren
        }
    }

    /**
     * Sets some data for this node.
     *
     * @param value
     *            - the data for this node (text that is found between the start
     *            and end tags of this node).
     */

    fun setDataValue(value:String){
        data = value
    }
}