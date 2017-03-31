package structure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Yangjiali on 2017/3/20 0020.
 * Version 1.0
 */
public class UserRecord {
    String userid;
    Map<String,Float> items;  //用户阅读记录及对应评分
    Map<String,Integer> tags;  //用户打过的标签及次数
    Set<ItemTag> itemTags;    //用户打过的itemtag

    public UserRecord() {
        items = new HashMap<>();
        tags = new HashMap<>();
        itemTags = new HashSet<>();
    }

    public UserRecord(String userid, Map<String,Float> items, Map<String,Integer> tags, Set<ItemTag> itemTags) {
        this.userid = userid;
        this.items = items;
        this.tags = tags;
        this.itemTags = itemTags;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
    public void addItem(String item,Float rate)
    {
        this.items.put(item,rate);
    }

    public Map<String, Float> getItems() {
        return items;
    }

    public void setItems(Map<String, Float> items) {
        this.items = items;
    }

    public void setTags(Map<String, Integer> tags) {
        this.tags = tags;
    }

    public void addTag(String tag,Integer times)
    {
        this.tags.put(tag.toLowerCase(),times);
    }

    public Map<String, Integer> getTags() {
        return tags;
    }

    public void addItemTag(String item, String tag)
    {
        ItemTag it = new ItemTag(item,tag.toLowerCase());
        itemTags.add(it);
    }

    public Set<ItemTag> getItemTags() {
        return itemTags;
    }

    public void setItemTags(Set<ItemTag> itemTags) {
        this.itemTags = itemTags;
    }
    public boolean isContainItem(String item)
    {
        return items.containsKey(item)?true:false;
    }
    public boolean isContainTag(String tag)
    {
        return tags.containsKey(tag.toLowerCase())?true:false;
    }
    public boolean isContainItemTag(ItemTag it)
    {
        for (ItemTag temp:itemTags)
        {
            if(temp.equals(it))
            {
                return true;
            }
        }
        return false;
    }
    public int getTagTimes(String tag)
    {
        return tags.get(tag.toLowerCase());
    }
    public boolean equals(String id)
    {
        return userid.equals(id) ? true : false;
    }
}
