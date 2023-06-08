import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestWithJUnit {
    @Test
    public void t1(){
        String str="iohoiho@aaaaaa iihi hihih @bbbbbb iggig gtigig";
        String userName=str.substring(str.indexOf("@")+1,(str.indexOf(" ",str.indexOf("@"))==-1) ? str.length() : str.indexOf(" ",str.indexOf("@")));
        assertEquals(userName,"aaaaaa");
    }

}
