package io.keyko.nevermined.cli.helpers;

public class ProgressBar extends Thread {

    private boolean doStop = false;

    private final int DEFAULT_SPINNER= 17;
    private int spinnerId= DEFAULT_SPINNER;


    private String[] spinners= {
            "←↖↑↗→↘↓↙",         // 0
            "▁▃▄▅▆▇█▇▆▅▄▃",     // 1
            "▉▊▋▌▍▎▏▎▍▌▋▊▉",    // 2
            "▖▘▝▗",             // 3
            "▌▀▐▄",             // 4
            "┤┘┴└├┌┬┐",         // 5
            "◢◣◤◥",             // 6
            "◰◳◲◱",             // 7
            "◴◷◶◵",             // 8
            "◐◓◑◒",             // 9
            "|/-\\",            // 10
            ".oO@*",            // 11
            "◜ ◝ ◞ ◟ ",         // 12
            "◇◈◆",              // 13
            "⣾⣽⣻⢿⡿⣟⣯⣷",       // 14
            "⡀⡁⡂⡃⡄⡅⡆⡇⡈⡉⡊⡋⡌⡍⡎⡏⡐⡑⡒⡓⡔⡕⡖⡗⡘⡙⡚⡛⡜⡝⡞⡟⡠⡡⡢⡣⡤⡥⡦⡧⡨⡩⡪⡫⡬⡭⡮⡯⡰⡱⡲⡳⡴⡵⡶⡷⡸⡹⡺⡻⡼⡽⡾⡿⢀⢁⢂⢃⢄⢅⢆⢇⢈⢉⢊⢋⢌⢍⢎⢏⢐⢑⢒⢓⢔⢕⢖⢗⢘⢙⢚⢛⢜⢝⢞⢟⢠⢡⢢⢣⢤⢥⢦⢧⢨⢩⢪⢫⢬⢭⢮⢯⢰⢱⢲⢳⢴⢵⢶⢷⢸⢹⢺⢻⢼⢽⢾⢿⣀⣁⣂⣃⣄⣅⣆⣇⣈⣉⣊⣋⣌⣍⣎⣏⣐⣑⣒⣓⣔⣕⣖⣗⣘⣙⣚⣛⣜⣝⣞⣟⣠⣡⣢⣣⣤⣥⣦⣧⣨⣩⣪⣫⣬⣭⣮⣯⣰⣱⣲⣳⣴⣵⣶⣷⣸⣹⣺⣻⣼⣽⣾⣿",
            "⠁⠂⠄⡀⢀⠠⠐⠈",       // 16
            "|/-\\"             // 17
    };



    public void run() {

        String anim= spinners[spinnerId];
        int x = 0;

        while (!doStop) {
            System.out.print("\r" + anim.charAt(x++ % anim.length()));
            try { Thread.sleep(100); }
            catch (Exception e) {}
        }
    }

    public synchronized void doStop() {
        this.doStop = true;
        System.out.println("");
    }

    public void setSpinner(int spinnerId) {
        this.spinnerId = spinnerId;
    }
}
