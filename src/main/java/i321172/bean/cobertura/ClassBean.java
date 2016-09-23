package i321172.bean.cobertura;

public class ClassBean extends PackageBean
{
    private String className;
    private String simpleName;
    private String fileName;
    private float  lineRate;
    private float  branchRate;
    private int    complexity;

    public void setClassName(String className)
    {
        this.className = className;
        int pos = className.lastIndexOf(".");
        setPackageName(className.substring(0, pos));
        setSimpleName(className.substring(pos + 1, className.length()));
    }

    public String getSimpleName()
    {
        return simpleName;
    }

    public void setSimpleName(String simpleName)
    {
        this.simpleName = simpleName;
    }

    public String getClassName()
    {
        return className;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public float getLineRate()
    {
        return lineRate;
    }

    public void setLineRate(float lineRate)
    {
        this.lineRate = lineRate;
    }

    public float getBranchRate()
    {
        return branchRate;
    }

    public void setBranchRate(float branchRate)
    {
        this.branchRate = branchRate;
    }

    public int getComplexity()
    {
        return complexity;
    }

    public void setComplexity(int complexity)
    {
        this.complexity = complexity;
    }

}
