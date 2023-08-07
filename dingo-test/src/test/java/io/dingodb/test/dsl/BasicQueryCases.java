/*
 * Copyright 2021 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dingodb.test.dsl;

import io.dingodb.calcite.DingoRootSchema;
import io.dingodb.test.dsl.builder.SqlTestCaseJavaBuilder;

public class BasicQueryCases extends SqlTestCaseJavaBuilder {
    public BasicQueryCases() {
        super("Basic");
    }

    @Override
    public void build() {
        table("i4k_vs_f80", file("i4k_vs_f80/create.sql"))
            .init(file("i4k_vs_f80/data.sql"), 9);

        table("i4k_vs_f80(empty)", file("i4k_vs_f80/create.sql"));

        table("i4k_vsk_f80", file("i4k_vsk_f80/create.sql"))
            .init(file("i4k_vs_f80/data.sql"), 9);

        table("i4k_vs0_i40_f80", file("i4k_vs0_i40_f80/create.sql"))
            .init(file("i4k_vs0_i40_f80/data.sql"), 2);

        table("i4k_vs_i40_f80_vs0_l0", file("i4k_vs_i40_f80_vs0_l0/create.sql"))
            .init(file("i4k_vs_i40_f80_vs0_l0/data.sql"), 2);

        table("i4k_vs0_vs0", file("i4k_vs0_vs0/create.sql"))
            .init(file("i4k_vs0_vs0/data_with_null.sql"), 1);

        table(
            "i4k_vs0_i40_i80_f40_f80_vs0_dt0_tm0_ts0_vs0_l0",
            file("i4k_vs0_i40_i80_f40_f80_vs0_dt0_tm0_ts0_vs0_l0/create.sql")
        )
            .init(file("i4k_vs0_i40_i80_f40_f80_vs0_dt0_tm0_ts0_vs0_l0/data.sql"), 10);

        test("Select all")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table}",
                csv(file("i4k_vs_f80/data.csv"))
            );

        test("Select all with schema prefixed")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from " + DingoRootSchema.DEFAULT_SCHEMA_NAME + ".{table}",
                csv(file("i4k_vs_f80/data.csv"))
            );

        test("Select filtered")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where amount > 4.0",
                csv(
                    "id, name, amount",
                    "INTEGER, STRING, DOUBLE",
                    "3, Cindy, 4.5",
                    "4, Doris, 5.0",
                    "5, Emily, 5.5",
                    "6, Alice, 6.0",
                    "7, Betty, 6.5",
                    "8, Alice, 7.0",
                    "9, Cindy, 7.5"
                )
            );

        test("Select projected")
            .use("table", "i4k_vs_f80")
            .step(
                "select name as label, amount * 10.0 as score from {table}",
                csv(
                    "label, score",
                    "STRING, DOUBLE",
                    "Alice, 35",
                    "Betty, 40",
                    "Cindy, 45",
                    "Doris, 50",
                    "Emily, 55",
                    "Alice, 60",
                    "Betty, 65",
                    "Alice, 70",
                    "Cindy, 75"
                )
            );

        test("Select filtered by primary key")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where id = 1",
                csv(
                    "id, name, amount",
                    "INT, STRING, DOUBLE",
                    "1, Alice, 3.5"
                )
            );

        test("Select filtered by primary key 1")
            .use("table", "i4k_vs0_i40_i80_f40_f80_vs0_dt0_tm0_ts0_vs0_l0")
            .step(
                "select * from {table} where id = 1",
                csv(
                    "id,name,age,gmt,price,amount,address,birthday,create_time,update_time,zip_code,is_delete",
                    "INT,STRING,INT,LONG,DOUBLE,DOUBLE,STRING,DATE,STRING,TIMESTAMP,STRING,BOOL",
                    "1,zhangsan,18,99,0.0,23.5,beijing,1998-04-06,08:10:10,2022-04-08 18:05:07,null,true"
                )
            );

        test("Select filtered by `or` of primary key")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where id = 1 or id = 2",
                csv(
                    "id, name, amount",
                    "INTEGER, STRING, DOUBLE",
                    "1, Alice, 3.5",
                    "2, Betty, 4.0"
                )
            );

        test("Select filtered by multiple primary keys")
            .use("table", "i4k_vsk_f80")
            .step(
                "select * from {table} where name between 'Betty' and 'Cindy'",
                csv(
                    "id, name, amount",
                    "INT, STRING, DOUBLE",
                    "2, Betty, 4.0",
                    "3, Cindy, 4.5",
                    "7, Betty, 6.5",
                    "9, Cindy, 7.5"
                )
            );

        test("Select filtered by `in list` of primary key")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where id in (1, 2, 3)",
                csv(
                    "id, name, amount",
                    "INTEGER, STRING, DOUBLE",
                    "1, Alice, 3.5",
                    "2, Betty, 4.0",
                    "3, Cindy, 4.5"
                )
            );

        test("Select filtered by `not in list` of primary key")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where id not in (3, 4, 5, 6, 7, 8, 9)",
                csv(
                    "id, name, amount",
                    "INTEGER, STRING, DOUBLE",
                    "1, Alice, 3.5",
                    "2, Betty, 4.0"
                )
            );

        test("Select filtered by `and` of conditions")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where id > 1 and name = 'Alice' and amount > 6",
                csv(
                    "id, name, amount",
                    "INTEGER, STRING, DOUBLE",
                    "8, Alice, 7.0"
                )
            );

        test("Select with mismatched type in filter")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where amount < 2147483648",
                csv(file("i4k_vs_f80/data.csv"))
            );

        test("Select with conflicting conditions")
            .use("table", "i4k_vs_f80")
            .step(
                "select * from {table} where name='Alice' and name='Betty'",
                csv(
                    "ID, NAME, AMOUNT",
                    "INT, STRING, DOUBLE"
                )
            );

        test("Cast double to int")
            .use("table", "i4k_vs_f80")
            .step("select id, name, cast(amount as int) as amount from {table}",
                csv(
                    "id, name, amount",
                    "INTEGER, STRING, INTEGER",
                    "1, Alice, 4",
                    "2, Betty, 4",
                    "3, Cindy, 5",
                    "4, Doris, 5",
                    "5, Emily, 6",
                    "6, Alice, 6",
                    "7, Betty, 7",
                    "8, Alice, 7",
                    "9, Cindy, 8"
                )
            );

        test("Function `case`")
            .use("table", "i4k_vs_f80")
            .step(
                file("i4k_vs_f80/select_case_when.sql"),
                csv(file("i4k_vs_f80/select_case_when.csv"))
            );

        test("Function `case` with multiple `when`")
            .use("table", "i4k_vs_f80")
            .step(
                file("i4k_vs_f80/select_case_when_1.sql"),
                csv(file("i4k_vs_f80/select_case_when_1.csv"))
            );

        test("Function `pow`")
            .use("table", "i4k_vs0_i40_f80")
            .step(
                " select pow(age, id) pai from {table}",
                csv(
                    "pai",
                    "DECIMAL",
                    "10",
                    "625"
                )
            );

        test("Operator `mod`")
            .use("table", "i4k_vs0_i40_f80")
            .step("select mod(amount, age) from {table}",
                csv(
                    "EXPR$0",
                    "DECIMAL",
                    "2.58",
                    "9.11"
                )
            );

        test("Concat null")
            .use("table", "i4k_vs0_vs0")
            .step(
                "select concat(s1, s2) as res from {table}",
                csv("res",
                    "STRING",
                    "NULL"
                )
            );

        test("Count of empty table")
            .use("table", "i4k_vs_f80(empty)")
            .step(
                "select count(amount) from {table}",
                csv(
                    "EXPR$0",
                    "LONG",
                    "0"
                )
            );

        test("Count group by bool")
            .use("table", "i4k_vs_i40_f80_vs0_l0")
            .step(
                "select is_delete, count(*) from {table} group by is_delete",
                csv(
                    "IS_DELETE, EXPR$1",
                    "BOOL, LONG",
                    "true, 3",
                    "false, 3",
                    "null, 2"
                )
            );

        test("Aggregation group by")
            .use("table", "i4k_vs_f80")
            .step(
                "select name, sum(amount) as `sum` from {table} group by name",
                csv(file("i4k_vs_f80/select_sum_group_by_name.csv"))
            );

        test("Aggregation of empty table")
            .use("table", "i4k_vs_f80(empty)")
            .step(
                "select sum(id) as `sum`, avg(amount) as `avg` from {table}",
                csv(
                    "SUM, AVG",
                    "INT, DOUBLE",
                    "NULL, NULL"
                )
            );

        test("Aggregation of multiple result")
            .use("table", "i4k_vs0_i40_i80_f40_f80_vs0_dt0_tm0_ts0_vs0_l0")
            .step(
                "select avg(age) aa, min(amount) ma, address from {table}" +
                    " where id in (1,3,5,7,9,13,35) or name<>'zhangsan' group by address order by ma limit 2",
                csv(
                    "AA, MA, ADDRESS",
                    "INT, DOUBLE, STRING",
                    "544, 0.0, 543",
                    "76, 2.3, beijing changyang"
                )
            );

        test("Aggregation")
            .use("table", "i4k_vs0_i40_i80_f40_f80_vs0_dt0_tm0_ts0_vs0_l0")
            .step(
                "select address, sum(amount) sa from {table}" +
                    " where address between 'C' and 'c' group by address",
                csv(
                    "address, sa",
                    "STRING, DOUBLE",
                    "beijing changyang, 2.3",
                    "beijing, 23.5",
                    "CHANGping, 9.0762556"
                )
            );

        test("Root selection")
            .step("create table {table}(name varchar(32) not null, age int, amount double, primary key(name))")
            .step(
                "insert into {table} values\n" +
                    "('Alice', 18, 3.5),\n" +
                    "('Betty', 22, 4.1),\n" +
                    "('Cindy', 39, 4.6),\n" +
                    "('Doris', 25, 5.2),\n" +
                    "('Emily', 24, 5.8)",
                count(5)
            ).step(
                "select name from {table} order by age",
                csv(
                    "name",
                    "STRING",
                    "Alice",
                    "Betty",
                    "Emily",
                    "Doris",
                    "Cindy"
                )
            );

        test("new test")
            .step(
                "CREATE TABLE {table} (\n" +
                    "    id int,\n" +
                    "    name varchar(64),\n" +
                    "    age int,\n" +
                    "    amount DOUBLE,\n" +
                    "    address varchar(255),\n" +
                    "    birthday DATE,\n" +
                    "    create_time TIME,\n" +
                    "    update_time TIMESTAMP,\n" +
                    "    is_delete boolean,\n" +
                    "    PRIMARY KEY (id)\n" +
                    ") "
            )
            .step(
                "insert into {table} values\n" +
                    "(1,'zhangsan',18,23.50,'beijing','1998-04-06','08:10:10','2022-04-08 18:05:07', true),\n" +
                    "(2,'lisi',25,895,' beijing haidian ','1988-02-05','06:15:08','2000-02-29 00:00:00', false),\n" +
                    "(3,'l3',55,123.123,'wuhan NO.1 Street','2022-03-04','07:03:15','1999-02-28 23:59:59', false),\n" +
                    "(4,'HAHA',57,9.0762556,'CHANGping', '2020-11-11', '05:59:59', '2021-05-04 12:00:00', True),\n" +
                    "(5,'awJDs',1,1453.9999,'pingYang1', '2010-10-01', '19:00:00', '2010-10-01 02:02:02', TRUE),\n" +
                    "(6,'123',544,0,'543', '1987-07-16', '01:02:03', '1952-12-31 12:12:12', true),\n" +
                    "(7,'yamaha',76,2.30,'beijing changyang', '1949-01-01', '00:30:08', '2022-12-01 01:02:03', False),\n" +
                    "(8,'zhangsan',18,12.3,'shanghai','2015-09-10', '03:45:10', '2001-11-11 18:05:07', true),\n" +
                    "(9,'op ',76,109.325,'wuhan', '1995-12-15', '16:35:38', '2008-08-08 08:00:00', true),\n" +
                    "(10,'lisi',256,1234.456,'nanjing', '2021-03-04', '17:30:15', '1999-02-28 00:59:59', FALSE),\n" +
                    "(11,'  aB c  dE ',61,99.9999,'beijing chaoyang', '1976-07-07', '06:00:00', '2024-05-04 12:00:00', false),\n" +
                    "(12,' abcdef',2,2345.000,'123', '2018-05-31', '21:00:00', '2000-01-01 00:00:00', true),\n" +
                    "(13,'HAHA',57,9.0762556,'CHANGping', '2014-10-13', '01:00:00', '1999-12-31 23:59:59', false),\n" +
                    "(14,'zhngsna',99,32,'chong qing ', '1949-10-01', '12:30:00', '2022-12-31 23:59:59', true),\n" +
                    "(15,'1.5',18,0.1235,'http://WWW.baidu.com','2007-08-15', '22:10:10', '2020-02-29 05:53:44', true),\n" +
                    "(16,' ',82,1999.99,'Huluodao', '1960-11-11', '14:09:49', '2000-02-29 00:00:00', false),\n" +
                    "(17,'',0,0.01,null, '2022-03-01', '15:20:20', '1953-10-21 16:10:28', true),\n" +
                    "(18,'tTATtt',181,18.18,' aabcaa ', '2020-11-11', '05:59:59', '2021-05-04 12:00:00', false),\n" +
                    "(19,'777',77,77.77,'7788', '2020-11-11', '05:59:59', '2021-05-04 12:00:00', false),\n" +
                    "(20,null,null,null,null, '1987-12-11', '11:11:00', '1997-07-01 00:00:00', null),\n" +
                    "(21,'Zala',76,2000.01,'JiZhou', '2022-07-07', '00:00:00', '2022-07-07 13:30:03', false)"
            )
            .step("select id,name,is_delete from {table} where is_delete in (false)");
    }
}