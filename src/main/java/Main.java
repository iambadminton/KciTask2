package main.java;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a.shipulin on 02.08.18.
 */
public class Main {
    public static void main(String[] args) {


        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        DataSource dataSource = context.getBean(DataSource.class);

        JdbcTemplate template = new JdbcTemplate(dataSource);
        template.execute("CREATE TABLE Customer(id INTEGER, name VARCHAR(100))");

        template.execute((ConnectionCallback<Object>) con -> {
            System.out.println(con.getMetaData().getURL());
            System.out.println(con.getMetaData().getUserName());
            //Not necessary to handle SQLException and close the connection

            return null;
        });

        template.execute("INSERT into customer(id, NAME ) VALUEs (1, 'John')");
        Boolean ok = template.execute((StatementCallback<Boolean>) stmt -> {
            stmt.execute("INSERT into customer(id, NAME ) VALUES (2, 'Edward')");
            return Boolean.TRUE;
        });
        System.out.println("ok=" + ok);


        template.execute((PreparedStatementCreator)con -> con.prepareStatement("INSERT into customer VALUES (?,?)"),
                (PreparedStatementCallback<Object>) ps -> {
                    ps.setInt(1, 3);
                    ps.setString(2, "Alex");
                    ps.execute();
                    return null;
                });




        NamedParameterJdbcTemplate namedTemplate = new NamedParameterJdbcTemplate(template);
        Map<String, Object> params = new HashMap<>();
        params.put("id", 4);
        params.put("name", "Nikola");
        namedTemplate.update("insert into customer values (:id, :name)", params);

        template.query("select * from customer", (RowCallbackHandler) rs -> {
            System.out.println(rs.getString("name"));
        });

        System.out.println("----------------------------");
        Map<String, Object> map = template.queryForMap("select * from customer where id=1");
        for(String key: map.keySet()) {
            System.out.println(key + " " + map.get(key));
        }

        System.out.println("-----------------------------");
        List<Map<String, Object>> res  = template.queryForList("select * from customer");
        for( Map m: res) {
            System.out.println(m.get("id") + "->" + m.get("name"));
        }



    }
}
