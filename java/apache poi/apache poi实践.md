# apache poi 实践

## 修改文本框内容并保留原格式

`XSLFTextRun`、`XSLFTextParagraph` 和 `XSLFTextShape` 是 Apache POI 中用于处理 PowerPoint 幻灯片中文本的不同类。

1. `XSLFTextShape`：表示幻灯片中的文本框。它是一个抽象类，可以作为其他文本相关类的基类。`XSLFTextShape` 继承自 `XSLFShape`，并包括多个方法用于管理文本内容、布局和样式等。
2. `XSLFTextParagraph`：表示文本框中的一个段落。段落是文本的分组单位，用于控制文本在文本框内的布局和格式。`XSLFTextParagraph` 是 `XSLFTextShape` 的子类，它代表了文本框中的一个段落，并提供了一些方法来获取和操作段落的属性、格式和内容。
3. `XSLFTextRun`：表示文本段落中的一个运行。运行是文本的基本单位，用于设置字体、样式、颜色和超链接等。`XSLFTextRun` 是 `XSLFTextParagraph` 的子类，用于表示段落中的文本运行，并提供了一些方法来获取和设置文本运行的属性和内容。

这三个类之间的区别如下：

- `XSLFTextShape` 代表整个文本框，包含多个段落。
- `XSLFTextParagraph` 代表文本框中的一个段落，包含多个运行。
- `XSLFTextRun` 代表在段落中具有相同格式和样式的连续文本。

关于获取文本的区别：

- `XSLFTextShape` 的 `getText` 方法将返回整个文本框的内容，包括所有段落和运行的文本。
- `XSLFTextParagraph` 的 `getText` 方法将返回该段落中的文本运行的合并文本内容。
- `XSLFTextRun` 的 `getText` 方法将返回具体文本运行的文本内容。

关于设置文本的区别：

- `XSLFTextShape` 的 `setText` 方法可以设置整个文本框的内容，将覆盖其中所有的段落和运行。
- `XSLFTextParagraph` 的 `setText` 方法不支持直接设置文本，而是通过操作其中的 `XSLFTextRun` 对象来更改文本运行的内容。
- `XSLFTextRun` 的 `setText` 方法用于设置具体的文本运行的内容。

总结起来，`XSLFTextShape` 表示整个文本框，`XSLFTextParagraph` 表示文本框中的一个段落，`XSLFTextRun` 表示一个段落中的一个运行。通过这些类，你可以逐级访问和操作幻灯片中的文本内容的不同层级，从整个文本框到段落和文本运行。

保留原格式只能通过修改 textRun 的文本内容实现。

```java
import org.apache.poi.xslf.usermodel.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        try (FileInputStream file = new FileInputStream("D:\\code\\java\\poi-test\\ten years.pptx")) {
            XMLSlideShow ppt = new XMLSlideShow(file);
            List<XSLFSlide> slides = ppt.getSlides();

            int i = 0;
            // 遍历每一页幻灯片
            for (XSLFSlide slide : slides) {
                // 获取幻灯片的形状列表
                List<XSLFShape> shapes = slide.getShapes();
                System.out.println("第" + (++i) + "张PPT,有" + shapes.size() + "个文本框");
                int j = 0;
                // 遍历每一个形状
                for (XSLFShape shape : shapes) {
                    // 判断是否是文本框类型
                    if (shape instanceof XSLFTextShape) {
                        System.out.println("第" + (++j) + "个文本框,id为"+shape.getShapeId());
                        System.out.println(((XSLFTextShape) shape).getText());
                    }
                }
                System.out.println("------------------------------------");
            }
            // 得到目标文本框
            XSLFShape shape = slides.get(2).getShapes().get(1);
            // 得到文本运行列表
            List<XSLFTextRun> textRuns = ((XSLFTextShape) shape).getTextParagraphs().get(0).getTextRuns();
            // 清空文本运行列表
            for (XSLFTextRun textRun:textRuns){
                textRun.setText("");
            }
            // 设置文本运行内容
            textRuns.get(0).setText("二〇二三年  8月6日");
            // 保存
            try(FileOutputStream out = new FileOutputStream("123.pptx")) {
                ppt.write(out);
            }
        }
    }
}
```

