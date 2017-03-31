import utils.DBHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Yangjiali on 2017/3/28 0028.
 * Version 1.0
 */
public class temp {
    public static void main(String[] args) throws SQLException {
        long starttime = System.currentTimeMillis();
        DBHelper dbHelper = new DBHelper("SELECT count(*) FROM user_ratedmovies WHERE movieId='10025' AND rate='1'");
        ResultSet resultSet = dbHelper.pst.executeQuery();
        while (resultSet.next()) {
            System.out.println(resultSet.getString(1));
        }
        long endtime = System.currentTimeMillis() - starttime;
        System.out.println("持续时间:"+endtime);
    }
}
