import java.io.*;

import java.sql.*;

import java.util.Scanner;

/**
 * @author 10jbro
 * @description: database
 * @create 2021/5/29 13:55
 */


public class main {


    public static void main(String [] args) throws FileNotFoundException {

        //随意输入
        System.out.println("enter a database name: ");
        Scanner s = new Scanner(System.in);
        String dbName = s.nextLine();

        DbUser myDbUser = null;
        myDbUser = new DbUser(dbName + ".db");


        PrintStream ps = System.out;

        // 将输出重定向到文件
        ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(dbName + ".txt")), true);
        System.setOut(ps);
        //将sql语句写入txt文件
        myDbUser.getTableNames();

        // 控制台再输出一遍
        System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(FileDescriptor.out)), true));
        myDbUser.getTableNames();


        //从txt建新db
        try {
            long start = System.currentTimeMillis();
            // 连接SQLite的JDBC
            Class.forName("org.sqlite.JDBC");

            // 建立一个数据库名.db的连接，如果不存在就在当前目录下创建之
            Connection conn = DriverManager
                    .getConnection("jdbc:sqlite:" + dbName + "new.db");

            conn.close();

            conn = DriverManager.getConnection("jdbc:sqlite:" + dbName + "new.db");

            Statement stat = conn.createStatement();

            //读txt文件
            File f = new File(dbName + ".txt");
            BufferedReader bf = new BufferedReader(new FileReader(f));
            String str;

            while((str=bf.readLine())!=null){
                String[] sent =str.split(";\n");

                //sent[0] =sent[0].replaceAll("\\'","");  //忽略单引号  不对，因为语句里面有单引号


                stat.executeUpdate(sent[0]);

            }

            conn.close(); // 结束数据库的连接

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

