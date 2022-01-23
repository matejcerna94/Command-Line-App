package org.matej;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class Demo {

  static final String JDBC_DRIVER = "org.h2.Driver";
  static final String DB_URL = "jdbc:h2:~/test";

  static final String USER = "sa";
  static final String PASS = "";

  static final Set<String> filterGradeOperators = Set.of("e", "l", "g");
  static final Set<String> filterNameOperators = Set.of("u", "l");
  static final Set<Integer> filterNameNUmberOfOperators = Set.of(2, 3);

  public static void main(String[] args) {
    Connection conn = null;
    Statement stmt = null;
    try {
      Class.forName(JDBC_DRIVER);

      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      stmt = conn.createStatement();
      String sql = "CREATE TABLE student(jmbg BIGINT PRIMARY KEY, ime VARCHAR(255), prezime VARCHAR(255), ocjena Smallint);";
      stmt.execute(sql);

      List<Student> osobe = readCSVFile(args[0]);

      for (Student student : osobe) {
        sql = String.format(
                "INSERT INTO student (jmbg,ime,prezime,ocjena) VALUES ( %s, '%s', '%s', %d)",
                student.getJmbg(), student.getIme(), student.getPrezime(), student.getOcjena());
        stmt.executeUpdate(sql);
      }

      String command = "";
      while (true) {
        if (command.equals("exit")) {
          break;
        }
        Scanner myObj = new Scanner(System.in);
        System.out.println("Unesite Naredbu: ");

        command = myObj.nextLine();

        String[] commandSplit = command.split(" ");

        switch (commandSplit[0]) {
          case "create":
            if(commandSplit.length != 5){
              System.out.println("Pogrešna naredba.\nMorate unijeti sve argumente!");
              break;
            }
            createStudent(stmt, commandSplit);
            break;
          case "read":
            if(commandSplit.length != 2){
              System.out.println("Pogrešna naredba.\nMorate unijeti sve argumente!");
              break;
            }
            readStudent(stmt, commandSplit);
            break;
          case "filter-grade":
            if(commandSplit.length != 3){
              System.out.println("Pogrešna naredba.\nMorate unijeti sve argumente!");
              break;
            }
            if(!filterGradeOperators.contains(commandSplit[1]) ){
              System.out.println("Prvi operator mora biti e, l ili g.");
              break;
            }
            filterGrade(stmt, commandSplit);
            break;
          case "filter-name":
            if(!filterNameNUmberOfOperators.contains(commandSplit.length) ){
              System.out.println("Pogrešna naredba.\nMorate unijeti sve argumente!");
              break;
            }
            if(commandSplit[2] != null && !filterNameOperators.contains(commandSplit[2]) ){
              System.out.println("Drugi operator mora biti 'u' ili 'l'.");
              break;
            }
            filterName(stmt, commandSplit);
            break;
          case "exit":
            return;
          default:
            System.out.println("Unesite valjanu naredbu.");
        }
      }

      conn.close();
    } catch (Exception se) {

      se.printStackTrace();
    }
    finally {

      try {
        if (stmt != null) {
          stmt.close();
        }
      } catch (SQLException ignored) {
      }
      try {
        if (conn != null) {
          conn.close();
        }
      } catch (SQLException se) {
        se.printStackTrace();
      }
    }
    File myObj = new File("C:\\Users\\Matej\\test.mv.db");
    myObj.delete();
    File myObj2 = new File("C:\\Users\\Matej\\test.trace.db");
    myObj2.delete();
    System.out.println("DB deleted!");
  }

  private static void filterName(Statement stmt, String[] commandSplit) throws SQLException {
    String sql;
    String commandFilterName = commandSplit[1];
    commandFilterName = commandFilterName + "%";
    sql = String.format(
            "select * from student where ime like '%s'", commandFilterName);
    ResultSet resultSet3 = stmt.executeQuery(sql);

    while(resultSet3.next()){
      if (commandSplit[2].equals("u")) {
        System.out.println(resultSet3.getString("ime").toUpperCase(Locale.ROOT) + " " +
                resultSet3.getString("prezime").toUpperCase(Locale.ROOT));
      } else if (commandSplit[2].equals("l")) {
        System.out.println(resultSet3.getString("ime").toLowerCase(Locale.ROOT) + " " +
                resultSet3.getString("prezime").toLowerCase(Locale.ROOT));
      }
    }
  }

  private static void filterGrade(Statement stmt, String[] commandSplit) throws SQLException {
    String sql;
    String operator = "";
    switch (commandSplit[1]) {
      case "l":
        operator = "<";
        break;
      case "g":
        operator = ">";
        break;
      case "e":
        operator = "=";
        break;
      default:
        break;
    }
    sql = String.format(
            "select * from student where ocjena %s %s", operator, commandSplit[2]);
    ResultSet resultSet2 = stmt.executeQuery(sql);
    while(resultSet2.next()){
      System.out.println("jmbg: " + resultSet2.getString("jmbg")
              + ", ime: " + resultSet2.getString("ime")
              + ", prezime: " + resultSet2.getString("prezime")
              + ", ocjena: " + resultSet2.getInt("ocjena"));
    }
  }

  private static void readStudent(Statement stmt, String[] commandSplit) throws SQLException {
    String sql = String.format(
            "select * from student where jmbg = %s", (commandSplit[1]));
    ResultSet resultSet = stmt.executeQuery(sql);

    resultSet.next();
    System.out.println("jmbg: "+
            resultSet.getString(1) + " ime: " + resultSet.getString(2) + " prezime: " + resultSet.getString(3)
                    + " ocjena: " + resultSet.getString(4));
  }

  private static void createStudent(Statement stmt, String[] commandSplit) throws SQLException {
    String sql = String.format(
            "INSERT INTO student (jmbg,ime,prezime,ocjena) VALUES ( %s, '%s', '%s', %d)",
            commandSplit[1], commandSplit[2], commandSplit[3],
            Integer.valueOf(commandSplit[4]));
    stmt.executeUpdate(sql);
    System.out.println("Student uspjesno kreiran. ");
  }

  public static List<Student> readCSVFile(String path) throws IOException {
    BufferedReader br;
    String line;
    List<Student> strList = new ArrayList<>();
    br = new BufferedReader(new FileReader(path));
    while ((line = br.readLine()) != null) {
      String[] split = line.split(";");
      Student student = new Student(split[0], split[1], split[2],
              Integer.valueOf(split[3]));
      strList.add(student);
    }
    return strList;
  }
}

