import algorithms.BayesScoring;
import structure.UserRecord;

import java.util.*;

/**
 * Created by Yangjiali on 2017/3/19 0019.
 */
public class test {
    public static void main(String[] args) {
        String pathname = System.getProperty("user.dir")+"\\data\\hetrectags.dat";
        String pathname1 = System.getProperty("user.dir")+"\\data\\ratings.dat";
        String itemscorepath = System.getProperty("user.dir")+"\\data\\moviedistribution.dat";
        //初始化所有物品的评分
        BayesScoring.initialItemScore(itemscorepath);
        //初始化用户的item-tag记录
        BayesScoring.initialUserItemTagMap(pathname);
        //初始化用户的阅读评分记录
        BayesScoring.initialUserRatingMap(pathname1);
        Map<String, UserRecord> userRecordMap = BayesScoring.getUsermap();
        System.out.println("所有用户数："+userRecordMap.size());
        float avgprecision = 0, avgrecall = 0;
        int usercounts = 0; //标签数大于阈值的用户才推荐
        //根据物品标签对寻找用户和物品
        for (String user : userRecordMap.keySet()) {
            if (userRecordMap.get(user).getItemTags().size() < 5)
            {
                continue;
            }
            else
            {
                usercounts++;
                Map<String,Float> itemtagmap = BayesScoring.getItemTagsScore(user);
                Map<String,Float> sortmap = sortByValue(itemtagmap);
                Iterator<Map.Entry<String, Float>> it = sortmap.entrySet().iterator();
                Map<String,Float> ur = userRecordMap.get(user).getItems();
                //根据提供的标签数计算推荐数
//            float recommendcounts = 400*(userRecordMap.get(user).getItemTags().size() / 20);
//            System.out.println();
                int hitcounts = 0,count = 0;
                while (it.hasNext())
                {
                    if (count++ > 349) break;
                    Map.Entry<String, Float> entry = it.next();
                    if (ur.containsKey(entry.getKey())) hitcounts++;
                    //System.out.println("itemtag:"+entry.getKey()+",score:"+entry.getValue());
                }
                System.out.println("推荐总数："+count);
                System.out.println("阅读总数："+ur.size()+"\n命中数："+hitcounts);
                float precision, recall;
                if (hitcounts != 0)
                {
                    precision = Float.valueOf(hitcounts) / Float.valueOf(count);
                    recall = Float.valueOf(hitcounts) / Float.valueOf(ur.size());
                }
                else
                {
                    precision = 0;
                    recall = 0;
                }
                avgprecision += precision;
                avgrecall += recall;
                System.out.println("用户" + user + "准确率：" + precision);
                System.out.println("用户" + user + "召回率：" + recall+"\n");
            }

        }
        System.out.println("计算的用户数："+usercounts);
        System.out.println("平均准确率："+avgprecision/usercounts+"  平均召回率："+avgrecall/usercounts);


        //根据物品寻找用户和物品（baseline）
//        Map<String, Float> itemmap = BayesScoring.getItemsScore("611");
//        Map<String,Float> sortmap = sortByValue(itemmap);
//        Iterator<Map.Entry<String,Float>> it = itemmap.entrySet().iterator();
//        Map<String,Float> ur = userRecordMap.get("611").getItems();
//        int hitcounts = 0,count = 0;
//        while (it.hasNext())
//        {
//            if (count++ > 400) break;
//            Map.Entry<String,Float> entry = it.next();
//            if (ur.containsKey(entry.getKey())) hitcounts++;
//            System.out.println("item:"+entry.getKey()+",score:"+entry.getValue());
//        }
//        System.out.println("推荐总数："+count);
//        System.out.println("阅读总数："+ur.size()+"\n命中数："+hitcounts);
        //计算所有用户的待推荐物品
//        int i=0;
//        for (String userid:userRecordMap.keySet())
//        {
//            i++;
//            if (i > 5) break;
//            Map<String, Float> itemscoremap = BayesScoring.getItemsScore(userid);
//            System.out.println("用户名："+userid);
//            Iterator<Map.Entry<String,Float>> it = itemscoremap.entrySet().iterator();
//            while (it.hasNext()) {
//                Map.Entry<String,Float> entry = it.next();
//                System.out.print(entry.getKey()+":"+entry.getValue()+"   ");
//            }
//        }

    }
    //对map根据value排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
    {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
        {
            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
            {
                return -(o1.getValue()).compareTo( o2.getValue() );
            }
        } );

        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list)
        {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }
}
