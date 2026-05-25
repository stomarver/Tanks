package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.mojang.takns.ImageConverter;

public class Text
{
    private static String[] data = { "" + //

            " ###  ####   #### ####  ##### #####  #### #   # #####     # #   # #     #   # #   #  ###  ####   ###  ####   #### ##### #   # #   # #   # #   # #   # ##### ", //
            "#   # #   # #     #   # #     #     #     #   #   #       # #  #  #     ## ## ##  # #   # #   # #   # #   # #       #   #   # #   # #   #  # #   # #     #  ", //
            "##### ####  #     #   # ###   ###   #  ## #####   #       # ###   #     # # # # # # #   # ####  #   # ####   ###    #   #   #  # #  # # #   #     #     #   ", //
            "#   # #   # #     #   # #     #     #   # #   #   #   #   # #  #  #     #   # #  ## #   # #     #  #  #  #      #   #   #   #  # #  # # #  # #    #    #    ", //
            "#   # ####   #### ####  ##### #      #### #   # #####  ###  #   # ##### #   # #   #  ###  #      ## # #   # ####    #    ###    #    # #  #   #   #   ##### ", //

            " ###    #   ####  ####  #   # #####  #### #####  ###   ###   #     # #   # #  ##  #     # #       ##   ##    ###   ###     #   #      #         ", //
            "#   #  ##       #     # #   # #     #         # #   # #   #  #     # #  ##### ## #     #   #     #       #   #       #    #     #     #         ", //
            "#   #   #    ###  ####  ##### ####  ####     #   ###   ####  #           # #    #     #     #    #       #   #       #   #       #    #         ", //
            "#   #   #   #         #     #     # #   #   #   #   #     #             #####  # ##  #       #   #       #   #       #    #     #     #         ", //
            " ###  ##### ##### ####      # ####   ###    #    ###  ####   #           # #  #  ## #         #   ##   ##    ###   ###     #   #      #         ", //
            
            "                                           #          ###   ##### ", //
            "                    #    # #   #     #     #     ###    #   #   # ", //
            "             ###   ###    #                            #    #   # ", //
            "       #            #    # #         #           ###        #   # ", //
            " #     #                       #     #                 #    ##### ", //
    };

    private static final String chars = "" + //
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + //
            "0123456789!\"#%/\\()[]<>| " + //
            ".,-+*:;'=?_";

    private static BufferedImage[][] images = new BufferedImage[chars.length()][8];
    private static BufferedImage[][] imageChars = new BufferedImage[256][8];

    public static void init()
    {
        for (int col = 0; col < 8; col++)
        {
            int charCount = 0;
            for (int row = 0; row < data.length / 5; row++)
            {
                for (int ch = 0; ch < data[row * 5].length() / 6; ch++)
                {
                    int[] pixels = new int[6 * 6];
                    for (int y = 0; y < 5; y++)
                        for (int x = 0; x < 5; x++)
                        {
                            char c = data[row * 5 + y].charAt(ch * 6 + x);
                            if (c == '#')
                            {
                                int r = (255 - (y) * 8) * ((col >> 0) & 1);
                                int g = (255 - (y) * 8) * ((col >> 1) & 1);
                                int b = (255 - (y) * 8) * ((col >> 2) & 1);
                                pixels[x + y * 6] = 0xff000000 + (r << 16) + (g << 8) + (b);
                                pixels[x + y * 6 + 1] = 0x90000000;
                                pixels[x + y * 6 + 7] = 0x90000000;
                                pixels[x + y * 6 + 6] = 0x90000000;
                            }
                        }

                    images[charCount][col] = ImageConverter.convert(6, 6, pixels, 2);
                    charCount++;
                }
            }
        }

        for (int i = 0; i < 256; i++)
        {
            if (chars.indexOf(i) >= 0)
            {
                imageChars[i] = images[chars.indexOf(i)];
            }
            else
            {
                imageChars[i] = images[chars.indexOf('_')];
            }
        }
    }

    public static void drawString(String string, Graphics g, int x, int y)
    {
        char[] chs = string.toUpperCase().toCharArray();
        g.setColor(new Color(1.0f, 0.5f, 0.2f, 0.3f));
        int col = 7;
        for (int i = 0; i < chs.length; i++)
        {
            if (chs[i] == '§')
            {
                col = chs[++i] - '0';
                continue;
            }
            if (chs[i] >= 0 && chs[i] < 256 && col >= 0 && col < 8)
            {
                g.drawImage(imageChars[chs[i]][col], x, y, null);
            }
            x += 6;
        }
    }
}