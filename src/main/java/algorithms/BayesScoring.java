package algorithms;

import structure.ItemTag;
import structure.UserRecord;
import utils.DBHelper;
import utils.FileIO;

import java.sql.ResultSet;
import java.util.*;

/**
 * Created by Yangjiali on 2017/3/24 0024.
 * Version 1.0
 */
public class BayesScoring {
    private static Map<String, UserRecord> usermap = new HashMap<>();
    private static Map<String,Float> itemscore = new HashMap<>();

    public static Map<String, UserRecord> getUsermap() {
        return usermap;
    }

    /**
     *
     * @param filepath item的评分
     */
    public static void initialItemScore(String filepath)
    {
        List<String> list = FileIO.readFileByLines(filepath);
        for (String str:list) {
            String[] record = str.split("::");
            itemscore.put(record[0],Float.valueOf(record[1]));
        }
    }

    /** <2>
     * 根据用户的标签寻找相应的物品
     * @param userid
     * @return
     */
    public static Map<String,Float> getItemscoreByTag(String userid)
    {
        Map<String,Float> itemsscore = new HashMap<>();
        Set<ItemTag> userITSet = usermap.get(userid).getItemTags();
        Map<String,Integer> tagSet = usermap.get(userid).getTags();
        for (ItemTag itemTag:userITSet)
        {
            //用户对这个标签的标记次数
            float usertagcount = tagSet.get(itemTag.getTag());
            for (UserRecord otheruser:usermap.values())
            {
                if (otheruser.isContainTag(itemTag.getTag()))
                {
                    for (ItemTag it:otheruser.getItemTags())
                    {
                        if (!it.getItem().equals(itemTag.getItem()))
                        {
                            if (!itemsscore.containsKey(it.getItem()))
                            {
                                itemsscore.put(it.getItem(), usertagcount);
                            }
                            else
                            {
                                itemsscore.put(it.getItem(),usertagcount+itemsscore.get(it.getItem()));
                            }
                        }
                    }
                }
            }
        }
        return itemsscore;
    }
    /**  <1>
     * @param userid 待推荐用户id
     * @return     推荐的item-tag
     */
    public static Map<String,Float> getItemTagsScore(String userid)
    {
        Map<String,Float> itemtagsscore = new HashMap<>();   //存放item-tag的评分
        Map<String,Float> itemsscore = new HashMap<>();      //存放item的评分
        UserRecord user = usermap.get(userid);
        for (ItemTag it:user.getItemTags())
        {
            //System.out.println("当前物品:"+it.getItem());
            //计算目标用户对该物品的评分作为权重，不考虑只打标签没有评分的物品
            if (user.getItems().get(it.getItem())==null)
            {
                continue;
            }
            else
            {
                float itemrate = user.getItems().get(it.getItem());
                float itscore = itemscore.get(it.getItem()) == null ? 1 / 2 : itemscore.get(it.getItem());
                float useritscore =  itscore;
                //System.out.println("物品"+it.getItem()+"评分："+itemrate);
                for (UserRecord otheruser:usermap.values())
                {
                    if (!otheruser.equals(user)&&otheruser.isContainItemTag(it))
                    {
                        //输出包含该itemtag的用户
                        //System.out.println("根据"+it.tostring()+"找到用户"+otheruser.getUserid());
                        for (ItemTag otherit:otheruser.getItemTags())
                        {
                            if (!otherit.getItem().equals(it.getItem())) {
                                float otheritscore = itemscore.get(otherit.getItem()) == null ? 1 / 2 : itemscore.get(otherit.getItem());
                                float currentitscore = otheritscore*useritscore;
                                //-------
                                if (!itemsscore.containsKey(otherit.getItem())) {
                                    itemsscore.put(otherit.getItem(),currentitscore);
                                }
                                else
                                {
                                    float score = itemsscore.get(otherit.getItem());
                                    itemsscore.put(otherit.getItem(),score+currentitscore);
                                }
                                //-------
//                            if (!itemtagsscore.containsKey(otherit.tostring())) {
//                                System.out.println("计算用户" + otheruser.getUserid() + "的" + otherit.tostring() + "得分为：" + currentitscore);
//                                itemtagsscore.put(otherit.tostring(), currentscore);
//                            } else {
//                                //当前item已有的分数加上新路径产生的分数
//                                float score = itemtagsscore.get(otherit.tostring()) + currentitscore;
//                                System.out.println("计算用户" + otheruser.getUserid() + "已存在的" + otherit.tostring() + "得分为：" + score);
//                                itemtagsscore.put(otherit.tostring(), score);
//                            }
                            }
                        }
                    }
                }
            }
        }
        //return itemtagsscore;
        return itemsscore;
    }

    /**
     * @param userid
     * @return user-item(打过标签的Item)-user-items之后得到的物品及评分集合
     */
    public static Map<String,Float> getItemscoreByItemTag(String userid)
    {
        Map<String, Float> itemMap = new HashMap<>();
        UserRecord userRecord = usermap.get(userid);
        Set<ItemTag> itemtags = userRecord.getItemTags();
        for (ItemTag it:itemtags)
        {
            //计算该物品的评分
            if (!userRecord.getItems().containsKey(it.getItem()))
            {
                continue;
            }
            else
            {
                float itscore = itemscore.get(it.getItem()) == null ? 1 / 2 : itemscore.get(it.getItem());
                float useritscore = userRecord.getItems().get(it.getItem()) * itscore;
                for (UserRecord user:usermap.values())
                {
                    if (!user.equals(userid)&&user.isContainItem(it.getItem()))
                    {
                        System.out.println("根据"+it.getItem()+"找到用户"+user.getUserid());
                        //计算该用户所有的item评分
                        for(ItemTag otherit:user.getItemTags())
                        {
                            float otheritscore = itemscore.get(otherit.getItem()) == null ? 1 / 2 : itemscore.get(otherit.getItem());
                            if (!itemMap.containsKey(otherit.getItem()))
                            {
                                System.out.println("计算物品"+otherit.getItem()+"的得分为:"+useritscore*otheritscore);
                                itemMap.put(otherit.getItem(),useritscore*otheritscore);
                            }
                            else
                            {
                                float score = itemMap.get(otherit.getItem()) + otheritscore * useritscore;
                                System.out.println("计算已存在物品"+otherit.getItem()+"的得分为:"+score);
                                itemMap.put(otherit.getItem(),score);
                            }
                        }
                    }
                }
            }

        }
        return itemMap;
    }
    /**
     * 问题！！！三步之后得到的物品太多
     * @param userid 待推荐用户id
     * @return    user-item-user-items之后得到的物品及评分集合
     */
    public static Map<String,Float> getItemsScore(String userid)
    {
        Map<String, Float> itemMap = new HashMap<>();
        UserRecord userRecord = usermap.get(userid);
        Map<String,Float> items = userRecord.getItems();
        Iterator<Map.Entry<String,Float>> it = items.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<String, Float> entry = it.next();
            //计算该物品的评分
            float useritscore = entry.getValue()*itemscore.get(entry.getKey());
            for (UserRecord user:usermap.values())
            {
                if (!user.equals(userid)&&user.isContainItem(entry.getKey()))
                {
                    System.out.println("根据"+entry.getKey()+"找到用户"+user.getUserid());
                    //计算该用户所有的item评分
                    for(String otherit:user.getItems().keySet())
                    {
                        float otheritscore = itemscore.get(otherit);
                        if (!itemMap.containsKey(otherit))
                        {
                            System.out.println("计算物品"+otherit+"的得分为:"+useritscore*otheritscore);
                            itemMap.put(otherit,useritscore*otheritscore);
                        }
                        else
                        {
                            float score = itemMap.get(otherit)+otheritscore*useritscore;
                            System.out.println("计算已存在物品"+otherit+"的得分为:"+score);
                            itemMap.put(otherit,score);
                        }
                    }
                }
            }
        }
        return itemMap;
    }

    public static float scoringFunction(String itemid)
    {
        DBHelper dbHelper1,dbHelper0;
        float Rp=0,Rm=0,score = 0;
        try {
            String sql1="SELECT count(*) FROM user_ratedmovies WHERE movieId='"+itemid+"' AND rate='1'";
            String sql0="SELECT count(*) FROM user_ratedmovies WHERE movieId='"+itemid+"' AND rate='-1'";
            dbHelper1 = new DBHelper(sql1);
            dbHelper0 = new DBHelper(sql0);
            ResultSet resSet1,resSet0;
            resSet1 = dbHelper1.pst.executeQuery();
            resSet0 = dbHelper0.pst.executeQuery();
            while (resSet1.next())
            {
                Rp = Float.valueOf(resSet1.getString(1));
            }
            while (resSet0.next())
            {
                Rm = Float.valueOf(resSet0.getString(1));
            }
            score = (Rp+1)/(Rp+Rm+1);
            //System.out.println("Rateplus:"+Rp+" Rateminus:"+Rm+" score:"+score);
            dbHelper0.close();
            dbHelper1.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return score;
    }
    /**
     * get recommended item-tag list after three-step for a user
     */
    public static Set<ItemTag> getItemTags(String userid)
    {
        UserRecord userRecord = usermap.get(userid);
        Set<UserRecord> userslist = new HashSet<>();  //存放两步之后可到达的用户
        Set<ItemTag> itemTagsList = userRecord.getItemTags();
        Set<ItemTag> itset = new HashSet<>();   //存放三步后所有可到达的item-tags
        for (ItemTag it:itemTagsList)
        {
            for (UserRecord user: usermap.values()) {
                if (!user.equals(userid)&&user.isContainItemTag(it))
                {
                    //userslist.add(user);
                    itset.addAll(user.getItemTags());
                    itset.remove(it);

                }
            }
        }
        //System.out.println("用户数："+userslist.size());
//        for (UserRecord user:userslist)
//        {
//            itset.addAll(user.getItemTags());
//        }
        return itset;
    }
    /**
     * initial rating record list of all users
     */
    public static void initialUserRatingMap(String filepath)
    {
        List<String> list = FileIO.readFileByLines(filepath);
        for (String str:list)
        {
            String[] record = str.split("::");
            if (!usermap.containsKey(record[0]))
            {
                UserRecord userRecord = new UserRecord();
                userRecord.setUserid(record[0]);
                userRecord.addItem(record[1],Float.valueOf(record[2]));
            }
            else
            {
                usermap.get(record[0]).addItem(record[1],Float.valueOf(record[2]));
            }
        }
    }
    /**
     * initial tag record list of all users
     * tag and corresponding times,item-tag
     */
    public static void initialUserItemTagMap(String filepath)
    {
        List<String> list = FileIO.readFileByLines(filepath);
        for (String str:list)
        {
            String[] record = str.split("::");
            if (!usermap.containsKey(record[0]))
            {
                UserRecord userRecord = new UserRecord();
                userRecord.setUserid(record[0]);
                userRecord.addTag(record[2],1);
                userRecord.addItemTag(record[1],record[2]);
                usermap.put(record[0],userRecord);
            }
            else
            {
                //如果已经包含该标签，则将该标签数加一
                if (usermap.get(record[0]).isContainTag(record[2]))
                {
                    int tagtimes = usermap.get(record[0]).getTagTimes(record[2]);
                    usermap.get(record[0]).addTag(record[2],tagtimes+1);
                    usermap.get(record[0]).addItemTag(record[1],record[2]);
                }
                else
                {
                    usermap.get(record[0]).addTag(record[2],1);
                    usermap.get(record[0]).addItemTag(record[1],record[2]);
                }

            }
        }
        System.out.println(list.size());
    }
}
