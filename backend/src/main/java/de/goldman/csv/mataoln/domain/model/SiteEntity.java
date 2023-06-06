package de.goldman.csv.mataoln.domain.model;

import jakarta.persistence.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Table(name="site")
@Entity
public class SiteEntity {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private UUID id;

    //               Domain,            Location on Site,  Tech Spend USD, Sales Revenue USD,  Social, Employees,               Company,                 Vertical, Tranco, Page Rank, Majestic, Umbrella, Telephones, Emails,                   Twitter,                       Facebook, LinkedIn, Google,                    Pinterest, GitHub,                       Instagram, Vk, Vimeo, Youtube, TikTok,                                              People,             City, State,    Zip, Country, First Detected,    Last Found,  First Indexed, Last Indexed, Exclusion,Compliance
    //"100percentfedup.com", "store.100percentfedup.com",           $500",                "", "10000",       "0", "100PercentFedUp.com", "Law  Govt And Politics", "1916",  "606365", "110287",  "68251",      "ph:",     "", "twitter.com/100percfedup", "facebook.com/100percentfedup",      "",     "", "pinterest.com/100percfedup",     "", "instagram.com/100percentfedup", "",    "",      "",     "", "N/A - Support - noemail; N/A - President - noemail", "Elizabeth Bay", "NSW", "2011",    "AU",    "2016-10-21", "2023-04-25",   "2012-04-25", "2023-05-02", "-", "-"
            //Domain, Location on Site, Tech Spend USD, Sales Revenue USD, Social, Employees
        //100percentfedup.com, store.100percentfedup.com, $500, , 10000, 0
    private String domain;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "locations", joinColumns = @JoinColumn(name = "location_id"))
    @Column(name = "location", nullable = false)
    private List<String> locationOnSite = new ArrayList<>();
    private String techSpendUsd;
    private String salesRevenueUsd;
    private Integer social;
    private Integer employees;

    @Lob
    @Column
    private byte[] company;

    private String vertical;
    private String tranco;
    private String pageRank;
    private String majestic;
    private String umbrella;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "telephones", joinColumns = @JoinColumn(name = "telephone_id"))
    @Column(name = "telephone", nullable = false)
    private List<String> telephones = new ArrayList<>();
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "emails", joinColumns = @JoinColumn(name = "email_id"))
    @Column(name = "email", nullable = false)
    private List<String> emails = new ArrayList<>();
    private String twitter;
    private String facebook;
    private String linkedIn;
    private String google;
    private String pinterest;
    private String gitHub;
    private String instagram;
    private String vk;
    private String vimeo;
    private String youtube;
    private String tikTok;
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "peoples", joinColumns = @JoinColumn(name = "people_id"))
    @Column(name = "people", nullable = false)
    private List<String> people = new ArrayList<>();
    private String city;
    private String state;
    private String zip;
    private String country;
    private LocalDate firstDetected;
    private LocalDate lastFound;
    private LocalDate firstIndexed;
    private LocalDate lastIndexed;
    private String exclusion;
    private String compliance;
    private String line;

    public SiteEntity() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getLocationOnSite() {
        return locationOnSite;
    }

    public void addLocationOnSite(String locationOnSite) {
        this.locationOnSite.add(locationOnSite);
    }

    public String getTechSpendUsd() {
        return techSpendUsd;
    }

    public void setTechSpendUsd(String techSpendUsd) {
        this.techSpendUsd = techSpendUsd;
    }

    public String getSalesRevenueUsd() {
        return salesRevenueUsd;
    }

    public void setSalesRevenueUsd(String salesRevenueUs) {
        this.salesRevenueUsd = salesRevenueUs;
    }

    public Integer getSocial() {
        return social;
    }

    public void setSocial(Integer social) {
        this.social = social;
    }

    public Integer getEmployees() {
        return employees;
    }

    public void setEmployees(Integer employees) {
        this.employees = employees;
    }

    public byte[] getCompany() {
        return company;
    }

    public void setCompany(byte[] company) {
        this.company = company;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public String getTranco() {
        return tranco;
    }

    public void setTranco(String tranco) {
        this.tranco = tranco;
    }

    public String getPageRank() {
        return pageRank;
    }

    public void setPageRank(String pageRank) {
        this.pageRank = pageRank;
    }

    public String getMajestic() {
        return majestic;
    }

    public void setMajestic(String majestic) {
        this.majestic = majestic;
    }

    public String getUmbrella() {
        return umbrella;
    }

    public void setUmbrella(String umbrella) {
        this.umbrella = umbrella;
    }

    public List<String> getTelephones() {
        return telephones;
    }

    public void addTelephones(String telephone) {
        if(!StringUtils.isEmpty(telephone) && !"ph:".equals(telephone)){
            this.telephones.add(telephone);
        }
    }

    public List<String> getEmails() {
        return emails;
    }

    public void addEmail(String email) {
        this.emails.add(email);
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        if(twitter != null && !StringUtils.isEmpty(twitter) && !twitter.contains("twitter")){
            throw new IllegalArgumentException("must contain twitter but was " + twitter);
        }
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        if(facebook != null && !StringUtils.isEmpty(facebook) && !facebook.contains("facebook")){
            throw new IllegalArgumentException("must contain facebook but was " + facebook);
        }
        this.facebook = facebook;
    }

    public String getLinkedIn() {
        return linkedIn;
    }

    public void setLinkedIn(String linkedIn) {
        if(linkedIn != null && !StringUtils.isEmpty(linkedIn) && !linkedIn.contains("linkedin")){
            throw new IllegalArgumentException("must contain linkedin but was " + linkedIn);
        }
        this.linkedIn = linkedIn;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        if(google != null && !StringUtils.isEmpty(google) && !google.contains("google")){
            throw new IllegalArgumentException("must contain google but was " + google);
        }
        this.google = google;
    }

    public String getPinterest() {
        return pinterest;
    }

    public void setPinterest(String pinterest) {
        if(pinterest != null  && !StringUtils.isEmpty(pinterest) &&  !pinterest.contains("pinterest")){
            throw new IllegalArgumentException("must pinterest google but was " + pinterest);
        }
        this.pinterest = pinterest;
    }

    public String getGitHub() {
        return gitHub;
    }

    public void setGitHub(String gitHub) {
        if(gitHub != null && !StringUtils.isEmpty(gitHub) && !gitHub.contains("github")){
            throw new IllegalArgumentException("must contain gitHub but was " + gitHub);
        }
        this.gitHub = gitHub;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        if(instagram != null && !StringUtils.isEmpty(instagram) && !instagram.contains("instagram")){
            throw new IllegalArgumentException("must contain instagram but was " + instagram);
        }
        this.instagram = instagram;
    }

    public String getVk() {
        return vk;
    }

    public void setVk(String vk) {
        this.vk = vk;
    }

    public String getVimeo() {
        return vimeo;
    }

    public void setVimeo(String vimeo) {
        this.vimeo = vimeo;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getTikTok() {
        return tikTok;
    }

    public void setTikTok(String tikTok) {
        this.tikTok = tikTok;
    }

    public List<String> getPeople() {
        return people;
    }

    public void addPeople(String people) {
        this.people.add(people);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDate getFirstDetected() {
        return firstDetected;
    }

    public void setFirstDetected(LocalDate firstDetected) {
        this.firstDetected = firstDetected;
    }

    public LocalDate getLastFound() {
        return lastFound;
    }

    public void setLastFound(LocalDate lastFound) {
        this.lastFound = lastFound;
    }

    public LocalDate getFirstIndexed() {
        return firstIndexed;
    }

    public void setFirstIndexed(LocalDate firstIndexed) {
        this.firstIndexed = firstIndexed;
    }

    public LocalDate getLastIndexed() {
        return lastIndexed;
    }

    public void setLastIndexed(LocalDate lastIndexed) {
        this.lastIndexed = lastIndexed;
    }

    public String getExclusion() {
        return exclusion;
    }

    public void setExclusion(String exclusion) {
        this.exclusion = exclusion;
    }

    public String getCompliance() {
        return compliance;
    }

    public void setCompliance(String compliance) {
        this.compliance = compliance;
    }

    @Override
    public String toString() {
        return "SiteEntity{" +
                "domain='" + domain + '\'' +
                ", locationOnSite='" + locationOnSite + '\'' +
                ", techSpendUsd='" + techSpendUsd + '\'' +
                ", salesRevenueUs='" + salesRevenueUsd + '\'' +
                ", social=" + social +
                ", employees=" + employees +
                ", company='" + company + '\'' +
                ", vertical='" + vertical + '\'' +
                ", tranco=" + tranco +
                ", pageRank=" + pageRank +
                ", majestic='" + majestic + '\'' +
                ", umbrella='" + umbrella + '\'' +
                ", telephones='" + telephones + '\'' +
                ", emails='" + emails + '\'' +
                ", twitter='" + twitter + '\'' +
                ", facebook='" + facebook + '\'' +
                ", linkedIn='" + linkedIn + '\'' +
                ", google='" + google + '\'' +
                ", pinterest='" + pinterest + '\'' +
                ", gitHub='" + gitHub + '\'' +
                ", instagram='" + instagram + '\'' +
                ", vk='" + vk + '\'' +
                ", vimeo='" + vimeo + '\'' +
                ", youtube='" + youtube + '\'' +
                ", tikTok='" + tikTok + '\'' +
                ", people='" + people + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", country='" + country + '\'' +
                ", firstDetected=" + firstDetected +
                ", lastFound=" + lastFound +
                ", firstIndexed=" + firstIndexed +
                ", lastIndexed=" + lastIndexed +
                ", exclusion='" + exclusion + '\'' +
                ", compliance='" + compliance + '\'' +
                '}';
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getLine() {
        return line;
    }
}
