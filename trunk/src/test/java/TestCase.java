import org.apache.commons.io.FilenameUtils;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * Date: 1/2/14
 * Time: 12:51
 * To change this template use File | Settings | File Templates.
 */
public class TestCase {

    @Test
    public void test1(){
        String uri = "http://img.dianziq.com/img/150x150f_yYzYne.jpg";
        String baseName = FilenameUtils.getBaseName(uri);
        System.out.println("baseName = " + baseName);
    }
}
