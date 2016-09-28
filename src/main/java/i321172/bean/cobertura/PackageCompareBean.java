package i321172.bean.cobertura;

public class PackageCompareBean extends PackageBean
{
    private PackageBean prePackageBean;

    public PackageBean getPrevPackageBean()
    {
        return prePackageBean;
    }

    public void setPrePackageBean(PackageBean prevPackageBean)
    {
        this.prePackageBean = prevPackageBean;
    }

}
