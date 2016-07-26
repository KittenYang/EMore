package com.caij.emore.bean;

import java.util.List;

public class SinaSearchRecommend
{
  String code;
  List<RecommendData> data;
  String msg;

  public String getCode()
  {
    return this.code;
  }

  public List<RecommendData> getData()
  {
    return this.data;
  }

  public String getMsg()
  {
    return this.msg;
  }

  public void setCode(String paramString)
  {
    this.code = paramString;
  }

  public void setData(List<RecommendData> paramList)
  {
    this.data = paramList;
  }

  public void setMsg(String paramString)
  {
    this.msg = paramString;
  }

  public static class RecommendData
  {
    String count;
    String key;

    public String getCount()
    {
      return this.count;
    }

    public String getKey()
    {
      return this.key;
    }

    public void setCount(String paramString)
    {
      this.count = paramString;
    }

    public void setKey(String paramString)
    {
      this.key = paramString;
    }
  }
}

/* Location:           C:\Users\Caij\Desktop\weico\0BEE18932511BC757BA4365FEF990A3C_classes_dex2jar.jar
 * Qualified Name:     com.eico.weico.flux.model.SinaSearchRecommend
 * JD-Core Version:    0.6.0
 */