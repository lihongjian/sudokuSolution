package next.section9;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Cell implements Serializable{

    private boolean issure = false;
    private int value = 0 ;
    private int[] maybeSet;
    private int x;
    private int y;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int[] getMaybeSet() {
        return maybeSet;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setMaybeSet(int[] maybeSet) {
        this.maybeSet = maybeSet;
    }

    public boolean getIssure() {
        return issure;
    }

    public void setIssure(boolean issure) {
        this.issure = issure;
    }

    public boolean checkIssure(){
        if(maybeSet.length == 1){
            value = maybeSet[0];
            issure = true;
        }
        if(maybeSet.length == 0 ){
            throw new CalErrorException();
        }
        return issure;
    }

    @Override
    public String toString() {
        if(issure){
            return String.valueOf(value);
        } else {
            return StringUtils.join(maybeSet,',');
        }
    }
}
