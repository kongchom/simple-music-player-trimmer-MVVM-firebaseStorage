package lib.mylibutils.model;

public class FontPacks {

    private String nameZip;
    private String md5Code;
    private int stt = 0;
    private String coverDefault;
    private String coverVi;
    private int totalFonts = 0;

    public String getNameZip() {
        return nameZip;
    }

    public void setNameZip(String nameZip) {
        this.nameZip = nameZip;
    }

    public String getMd5Code() {
        return md5Code;
    }

    public void setMd5Code(String md5Code) {
        this.md5Code = md5Code;
    }

    public int getStt() {
        return stt;
    }

    public void setStt(int stt) {
        this.stt = stt;
    }

    public String getCoverDefault() {
        return coverDefault;
    }

    public void setCoverDefault(String coverDefault) {
        this.coverDefault = coverDefault;
    }

    public String getCoverVi() {
        return coverVi;
    }

    public void setCoverVi(String coverVi) {
        this.coverVi = coverVi;
    }

    public int getTotalFonts() {
        return totalFonts;
    }

    public void setTotalFonts(int totalFonts) {
        this.totalFonts = totalFonts;
    }
}
