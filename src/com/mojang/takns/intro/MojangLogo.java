package com.mojang.takns.intro;

import java.awt.Color;
import java.awt.Graphics;

import com.mojang.takns.Takns;
import com.mojang.takns.gui.Text;

public class MojangLogo
{
    String parts[] = {
            "\u0071E\u0071>\u00729\u00726\u00735\u00755\u00766\u00788"+
            "\u007c>\u007fA\u0081D\u0084F\u0087G\u008aH\u008dG\u0090E"+
            "\u0092D\u0094D\u0095F\u0097I\u0098M\u009aQ\u009bV\u009c\\"+
            "\u009da\u009ek\u009f\u0079\u00a0\u007a\u0090\u0072\u0090\u0071\u0090\u0070\u008fn"+
            "\u008fm\u008ek\u008di\u008cg\u008ad\u0088b\u0086_\u0084]"+
            "\u0080Z\u007dW\u0079T\u0073QnOhMcL^K"+
            "XLSLONKPGRCU@X>\\"+
            "<a;f;k<\u0075?\u007dD\u0084J\u008aR\u008e"+
            "[\u0091d\u0093m\u0094\u0076\u0094\u0080\u0094\u0088\u0094\u0090\u0093\u0096\u0092"+
            "\u009b\u0091\u009e\u0091\u00a0\u0090\u009f\u0091\u009f\u0092\u009f\u0093\u009f\u0094\u009f\u0095"+
            "\u009e\u0097\u009e\u0098\u009d\u0099\u009c\u009b\u009a\u009c\u0099\u009d\u0097\u009e\u0095\u009f"+
            "\u0093\u009f\u0090\u00a03\u00a00\u009f.\u009f,\u009e*\u009d(\u009c"+
            "'\u009b&\u0099%\u0098%\u0097$\u0095$\u0094$\u0093#\u0092"+
            "#\u0091#\u0090#E$B$@%>&<';"+
            "(:*9+8-7.7/70616"+
            "2636R6U6X7[7]8`:"+
            "c;e<g>i?k@mBnCoD"+
            "\u0070E\u0071E",

            "\u00816\u00815\u00814\u00812\u00821\u0082/\u0082-\u0083+"+
            "\u0083)\u0084'\u0084&\u0085$\u0086\"\u0086!\u0087 \u0088 "+
            "\u0089 \u008a \u008b!\u008c\"\u008c$\u008d&\u008d'\u008e)"+
            "\u008e+\u008f-\u008f/\u00901\u00902\u00904\u00905\u00906"+
            "\u00907\u00908\u00909\u008f:\u008e;\u008d<\u008c=\u008b="+
            "\u008a=\u0089>\u0088>\u0087=\u0086=\u0085=\u0084<\u0083;"+
            "\u0082:\u00829\u00818\u00817\u00816",
    
            };
    
    int[][] xs = new int[2][];
    int[][] ys = new int[2][];
    int[] count = new int[2];
    
    public MojangLogo()
    {
        parse();
    }
    
    public void parse()
    {
        for (int i=0; i<2; i++)
        {
            count[i] = parts[i].length()/2;
            xs[i] = new int[count[i]];
            ys[i] = new int[count[i]];
            
            for (int j=0; j<count[i]; j++)
            {
                xs[i][j] = (parts[i].charAt(j*2+0)-32)+(Takns.SCREEN_WIDTH-128)/2;
                ys[i][j] = (parts[i].charAt(j*2+1)-32)+(Takns.SCREEN_HEIGHT-128)/2-12;
            }
        }
    }
    
    public void render(Graphics g)
    {
        g.setColor(new Color(0xff5a5155));
        g.fillRect(0, 0, Takns.SCREEN_WIDTH, Takns.SCREEN_HEIGHT);
        
        g.setColor(new Color(0x40000000));
        g.fillPolygon(xs[0], ys[0], count[0]);
        g.fillPolygon(xs[1], ys[1], count[1]);
        
        g.translate(-1, -1);
        
        g.setColor(Color.WHITE);
        g.fillPolygon(xs[0], ys[0], count[0]);
        g.fillPolygon(xs[1], ys[1], count[1]);

        String title = "MOJANG SPECIFICATIONS";
        Text.drawString(title, g, Takns.SCREEN_WIDTH/2-title.length()*3+2, Takns.SCREEN_HEIGHT/2+58);
    }
}