package org.matej;

public class Student {

  String jmbg;

  String ime;

  String prezime;

  Integer ocjena;

  public Student(String jmbg, String ime, String prezime, Integer ocjenu) {
    this.jmbg = jmbg;
    this.ime = ime;
    this.prezime = prezime;
    this.ocjena = ocjenu;
  }

  public String getJmbg() {
    return jmbg;
  }

  public void setJmbg(String jmbg) {
    this.jmbg = jmbg;
  }

  public String getIme() {
    return ime;
  }

  public void setIme(String ime) {
    this.ime = ime;
  }

  public String getPrezime() {
    return prezime;
  }

  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }

  public Integer getOcjena() {
    return ocjena;
  }

  public void setOcjena(Integer ocjena) {
    this.ocjena = ocjena;
  }
}
