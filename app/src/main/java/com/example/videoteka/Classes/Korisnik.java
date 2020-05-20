package com.example.videoteka.Classes;

public class Korisnik {
    public String Ime;
    public String Prezime;
    public String Adresa;
    public String Rola;
    public double Balance;
    public String SlikaURL;
    public String DatumRodjenja;
    public String Spol;
    public String KorisnickoIme;

    public Korisnik() {
    }

    public Korisnik(String ime, String prezime, String adresa, String rola, double balance, String slikaURL, String datumRodjenja, String spol, String korisnickoIme) {
        Ime = ime;
        Prezime = prezime;
        Adresa = adresa;
        Rola = rola;
        Balance = balance;
        SlikaURL = slikaURL;
        DatumRodjenja = datumRodjenja;
        Spol = spol;
        KorisnickoIme = korisnickoIme;
    }

    public Korisnik(String ime, String prezime, String adresa, String rola, String korisnickoIme, String url, double balance) {
        Ime = ime;
        Prezime = prezime;
        Adresa = adresa;
        Rola = rola;
        KorisnickoIme = korisnickoIme;
        SlikaURL = url;
        Balance = balance;
    }

    public Korisnik(String rola, String korisnickoIme, String url,double balance) {
        Rola = rola;
        KorisnickoIme = korisnickoIme;
        SlikaURL = url;
        Balance = balance;
        Spol = " ";
        DatumRodjenja = "";
        Ime = "";
        Prezime = "";
    }
}
