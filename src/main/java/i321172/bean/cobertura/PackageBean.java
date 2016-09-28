package i321172.bean.cobertura;

public class PackageBean
{
    private String period;
    private String owner;
    private String feature;
    private String packageName;
    private double totalCoverage;
    private double lineCoverage;
    private double branchCoverage;
    private int    lines;
    private int    coverLines;
    private int    branches;
    private int    coverBranches;
    private int    classNum;
    private int    methodNum;
    private int    toBeCoveredLines;
    private int    toBeCoveredBranches;

    public String getPackageName()
    {
        return packageName;
    }

    public void setPackageName(String packageName)
    {
        this.packageName = packageName;
    }

    public double getTotalCoverage()
    {
        return totalCoverage;
    }

    public void setTotalCoverage(double totalCoverage)
    {
        this.totalCoverage = totalCoverage;
    }

    public double getLineCoverage()
    {
        return lineCoverage;
    }

    public void setLineCoverage(double lineCoverage)
    {
        this.lineCoverage = lineCoverage;
    }

    public double getBranchCoverage()
    {
        return branchCoverage;
    }

    public void setBranchCoverage(double branchCoverage)
    {
        this.branchCoverage = branchCoverage;
    }

    public int getLines()
    {
        return lines;
    }

    public void setLines(int lines)
    {
        this.lines = lines;
    }

    public int getCoverLines()
    {
        return coverLines;
    }

    public void setCoverLines(int coverLines)
    {
        this.coverLines = coverLines;
    }

    public int getBranches()
    {
        return branches;
    }

    public void setBranches(int branches)
    {
        this.branches = branches;
    }

    public int getCoverBranches()
    {
        return coverBranches;
    }

    public void setCoverBranches(int coverBranches)
    {
        this.coverBranches = coverBranches;
    }

    public String getPeriod()
    {
        return period;
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }

    public int getClassNum()
    {
        return classNum;
    }

    public void setClassNum(int classNum)
    {
        this.classNum = classNum;
    }

    public int getMethodNum()
    {
        return methodNum;
    }

    public void setMethodNum(int methodNum)
    {
        this.methodNum = methodNum;
    }

    public int getToBeCoveredLines()
    {
        return toBeCoveredLines;
    }

    public void setToBeCoveredLines(int toBeCovered)
    {
        this.toBeCoveredLines = toBeCovered;
    }

    public int getToBeCoveredBranches()
    {
        return toBeCoveredBranches;
    }

    public void setToBeCoveredBranches(int toBeCoveredBranches)
    {
        this.toBeCoveredBranches = toBeCoveredBranches;
    }

    public String getFeature()
    {
        return feature;
    }

    public void setFeature(String feature)
    {
        this.feature = feature;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
    }
}
