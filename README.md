
>fastcrawler是一个快速简单，可扩展性强的多线程网络爬虫，它能一定程度上简化了爬虫的爬取流程，开发者能快速的爬取任何想爬取的内容，而无需关注请求处理，线程控制等非业务处理，
并能够进行定制

fastcrawler的一些特点:

* 基于多线程
* 使用jsoup库，支持css选择器抽取页面指定内容
* 采用httpclient库，对http请求和连接管理完全封装
* 支持下载二进制数据，如图片,pdf等,并支持文本和json数据处理
* 支持简单的去重处理，对页面的抽取结果可灵活定制处理
* 定制爬取时间间隔，防止请求过快导致被服务器识别
* 支持http请求代理

### 简单示例

#### 爬取简书文章内容

PageHandler是fastcrawler的一个组件，代表一个页面处理器，fastcrawler将整个页面下载后，都会交给PageHandler进行处理
，AbstractPageHandler是PageHandler的一个子类，包含了默认的处理流程，一般需要继承这个类，下面是一个爬取[简书网站](www.jianshu.com)的文章标题和文章内容的一个示例：


```java
public class JianShuHandler extends AbstractPageHandler {

    public static void main(String[] args) {
        CrawlerConfig crawlerConfig = CrawlerConfig.custom().setMaxDepth(10);
        Crawler.create(crawlerConfig).
                setThreadCount(5).
                addRootUrl("http://www.jianshu.com/").
                startSync(new JianShuHandler());
    }

    /**
     *
     */
    public boolean handleHtml(Page page) {
        String passage = page.getHtmlData().css(".note .article .show-content").get();
        String title = page.getHtmlData().css(".note .article .title").get();
        if (passage != null) {
            page.addResult("title", title);
            page.addResult("passage", passage);
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldVisit(Page page, CrawlerRequest request) {
        return request.getUrl().matches("http://www\\.jianshu\\.com/p/.+");
    }
}
```

这里的handlerHtml方法表示对页面进行处理，并将页面的抽取内容进行存储在page中，这里可用css选择器进行抽取，返回的布尔值表示该页面的抽取结果是否进行处理，
shouldVisit方法表示该页面上有哪些链接可作为后序爬取请求,这里限定了爬取的url规则. Crawler是整个爬虫的入口，它可以进行添加根url，设置线程数等操作，执行startSync方法，传入PageHandler对象就可开始爬取, 
CrawlerConfig对象代表着爬虫的所有配置。

>> startSync方法代表异步运行爬虫，若执行start方法，当前线程会阻塞

#### 爬取图片

fastcrawler可以下载二进制数据，如图片等，你可以简单的下载一张图片，如：
````java
public class DownloadImg {

    public static void main(String[] args) {
        Crawler.downLoad(
                "http://edu-image.nosdn.127.net/8E12914771C3A24DEB20C8049DEDBA73.png?imageView&thumbnail=225y142&quality=100" ,
                "H://fastcrawler");
    }
}
````

通过Crawler的download方法就可以快速的下载请求的二进制数据，也可以通过爬取流程，下载所有图片，下面是一个快速下载美女网站的所有美女图片的一个示例：

```java
public class MulDownloadImgs extends BinaryPageHandler{

    public static void main(String[] args) {
        CrawlerConfig config = CrawlerConfig.custom().setIncludeBinaryContent(true).setBinaryStorePath("H://fastcrawler");
        Crawler crawler = Crawler.create(config);
        crawler.setThreadCount(5).addRootUrl("http://www.7160.com/").start(new MulDownloadImgs());
    }

    @Override
    public boolean filter(Page page) {
        return page.getBinaryData().getExtenstion().matches("\\.jpg|\\.png|\\.gif");
    }

    @Override
    public boolean shouldVisit(Page page, CrawlerRequest request) {
        return true;
    }

}
```

这里BinaryPageHandler是AbstractPageHandler的一个子类，包含了默认的内容处理，通过filter方法可以过滤我们想要的数据，这个示例是根据后缀名获取图片，
另外，需要将CrawlerConfig的includeBinaryContent设置成true以便fastcrawler可以处理二进制数据，并设置存储路径，通过startSync方法执行后，就会在指定的文件路径下得到想要的内容


### fastcrawler的一些增强计划

* 支持分布式爬取
* 可爬取js动态渲染的内容
* 提供一些注解，简化页面的内容抽取
* 对爬虫的url调度和去重处理进行更灵活的策略(如通过Redis)
* 提供一些灵活的爬虫配置，如模拟登陆，cookie配置

###联系我

* 邮箱 : doubleview@163.com
