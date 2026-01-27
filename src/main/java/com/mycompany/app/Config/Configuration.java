package com.mycompany.app.Config;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Configuration {
    public Integer port;
    public String AppName;
    public String baseDir = "./www";
    public String jdbcPostgresPort = "5432";
    public String jdbcPostgresHost = "localhost";
    public String jdbcPostgresDatabase = "postgres";
    public String jdbcPostgresUser = "postgres";
    public String jdbcPostgresPassword = "password";

    public Configuration() {

    }

}
