package oneway.sim;

import java.io.*;
import java.util.*;

public class OnewayGUI extends Oneway
{
    // the html of current state
    public String state() {
        int pixels = 8000;
        String title = "Oneway";
        StringBuffer buf = new StringBuffer("");
		buf.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n");
		buf.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"en-US\" xml:lang=\"en\">\n");
		buf.append("<head>\n");
		buf.append(" <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-7\" />\n");
		buf.append(" <title>" + title + "</title>\n");
		buf.append(" <style type=\"text/css\">\n");
		buf.append("  a:link {text-decoration: none; color: blue;}\n");
		buf.append("  a:visited {text-decoration: none; color: blue;}\n");
		buf.append("  a:hover {text-decoration: none; color: red;}\n");
		buf.append("  a:active {text-decoration: none; color: blue;}\n");
        // right arrow green
		buf.append("  .arrow-right-green {width:0;height:0;border-top:20px solid transparent;border-bottom:20px solid transparent;border-left:20px solid green;float:left;}\n");
        // right arrow red
		buf.append("  .arrow-right-red {width:0;height:0;border-top:20px solid transparent;border-bottom:20px solid transparent;border-left:20px solid red; float:left;}\n");
        // left arrow green
		buf.append("  .arrow-left-green {width:0;height:0;border-top:20px solid transparent;border-bottom:20px solid transparent;border-right:20px solid green; float:left;}\n");
        // left arrow red
		buf.append("  .arrow-left-red {width:0;height:0;border-top:20px solid transparent;border-bottom:20px solid transparent;border-right:20px solid red; float:left;}\n");
        // parking lot
        buf.append("  div.parking {width:40px;height:40px;float:left;display:inline;}");
        // empty block
        buf.append("  div.empty {width:48px;height:20px;float:left;border:1px solid black;background-color:gray}\n");
        // left bound block
        buf.append("  div.leftbound {width:50px;height:20px;float:left;display:inline}\n");
        // right bound block
        buf.append("  div.rightbound {width:50px;height:20px;float:left;display:inline}\n");
        // spaces between block
        buf.append("  div.blockspace {width:5px;height:40px;float:left;}\n");
        // cars in the parking lot
        buf.append("  div.leftcar {width:40px;height:40px;text-align:center;font-size:35px;background-color:blue}\n");
        buf.append("  div.rightcar {width:40px;height:40px;text-align:center;font-size:35px:background-color:red}\n");
        
        // parking capacity
        buf.append("  div.capacity {width:38px;height:40px;text-align:center:font-size:50;border:1px solid black;font-weight: bold;font-family: 'Comic Sans MS', cursive, sans-serif}\n");

		buf.append(" </style>\n");
		buf.append("</head>\n");
		buf.append("<body>\n");

		// general part
        buf.append(" <div style=\"width:" + pixels + "px; margin-left:auto; margin-right: auto;\">\n");

		// button 1
		buf.append("   <div style=\"width: 200px; height: 70px; float:left; cursor: pointer; text-align: center; font-size: 40px;\n");
		buf.append("               font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\"><a href=\"play\">Play</a></div>\n");
		// button 2
		buf.append("   <div style=\"width: 200px; height: 70px; float:left; cursor: pointer; text-align: center; font-size: 40px;\n");
		buf.append("               font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\"><a href=\"stop\">Stop</a></div>\n");
		// button 3
		buf.append("   <div style=\"width: 200px; height: 70px; float:left; cursor: pointer; text-align: center; font-size: 40px;\n");
		buf.append("               font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\"><a href=\"step\">Step</a></div>\n");
        buf.append("   <div style=\"clear:both;\"></div>\n");

        // Delivered cars
		buf.append("   <div style=\"width: 500x; height: 70px; float:left; text-align: left; font-size: 25px;font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">");
        buf.append("Delivered cars:" + deliveredCars);
        buf.append("</div>\n");
        buf.append("   <div style=\"clear:both;\"></div>\n");

        // Time:
		buf.append("   <div style=\"width: 500x; height: 70px; float:left; text-align: left; font-size: 25px;font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">");
        buf.append("Time:" + tick);
        buf.append("</div>\n");
        buf.append("   <div style=\"clear:both;\"></div>\n");

        printMain(buf);
        
		buf.append(" </div>\n");
		buf.append("</body>\n");
		buf.append("</html>\n");
		return buf.toString();
    }


    private void printMain(StringBuffer buf) {
        // the main div
        buf.append(" <div>\n");

        // print left lights
        printLights(buf, "left", llights);

        // vertical space
        buf.append("  <div style=\"width: 400px; height: 50px; float:left;\"></div>\n");
        buf.append("  <div style=\"clear:both\"></div>\n");

        printRoads(buf);

        // vertical space
        buf.append("  <div style=\"width: 400px; height: 50px; float:left;\"></div>\n");
        buf.append("  <div style=\"clear:both\"></div>\n");

        // print right lights
        printLights(buf, "right", rlights);

        // vertical space
        buf.append("  <div style=\"width: 400px; height: 50px; float:left;\"></div>\n");
        buf.append("  <div style=\"clear:both\"></div>\n");


        // print parking lots info
        for (int i = 0; i < capacity.length; i++) {
            // draw the parking lot
            // a parking lot is represented by
            // 1) capacity 2) rightbound cars 3) leftbound cars
            buf.append("  <div style=\"width:40px;height:120px;float:left;\">\n");
            // capacity
            buf.append("   <div class=\"capacity\">");
            if (i == 0 || i == capacity.length-1)
                buf.append("INF");
            else
                buf.append(capacity[i]);
            buf.append("</div>");
            // leftbound
            buf.append("   <div class=\"capacity\">");
            if (i != 0)
                buf.append(left[i].size());
            else
                buf.append(0);
            buf.append("</div>");
            // rightbound
            buf.append("   <div class=\"capacity\">");
            if (i != nsegments)
                buf.append(right[i].size());
            else
                buf.append(0);
            buf.append("</div>");

            buf.append("  </div>");
            // draw an empty space
            int spaces = nblocks * 50 + (nblocks+1) * 5;
            buf.append("  <div style=\"width:" + spaces + "px;height:120px;float:left;\"></div>\n");
            
        }
        
        buf.append("   <div style=\"clear:both;\"></div>\n");
        buf.append("   <div style=\"height: 100px\"></div>\n");
        buf.append("   <div style=\"clear:both;\"></div>\n");
        

        // if game ends
        // show the penalty
        if (deliveredCars == cars.size()) {
            buf.append("   <div style=\"width: 500px; height: 70px; float:left; text-align: left; font-size: 25px;font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">");
            buf.append(String.format("Player penalty: %.2f", penalty));
            buf.append("</div>\n");
            buf.append("   <div style=\"clear:both;\"></div>\n");
        }

        // if something is run
        if (errmsg != null) {
            buf.append("   <div style=\"width: 500px; height: 70px; float:left; text-align: left; font-size: 25px;font-weight: bold; font-family: 'Comic Sans MS', cursive, sans-serif\">");
            buf.append(errmsg);
            buf.append("</div>\n");
            buf.append("   <div style=\"clear:both;\"></div>\n");
        }
    }

    private void printRoads(StringBuffer buf) {
        // print every segment
        buf.append("  <div id=\"road\">\n");
        for (int i = 0; i < nsegments; i++) {
            // print the parking lot
            buf.append("   <div class=\"parking\">");
            buf.append("<img src=\"oneway/parking.png\" width=\"40\" height=\"40\">");
            buf.append("</div>\n");

            // print each block
            for (int j = 0; j < nblocks; j++) {
                // print a small space
                buf.append("   <div class=\"blockspace\"></div>\n");
                if (segments[i][j] == null) {
                    // print an empty space
                    buf.append("   <div class=\"empty\"></div>\n");
                }
                else {
                    // print other
                    String dir = (segments[i][j].dir > 0) ? "rightbound" : "leftbound";
                    String img = (segments[i][j].dir > 0) ? "oneway/rightcar.jpg" : "oneway/leftcar.png";

                    buf.append("   <div style=\"width:50px;height:80px;float:left\">");
                    // the car
                    buf.append("   <div class=\"" + dir + "\">");
                    //                    buf.append(segments[i][j].startTime);
                    buf.append("<img src=\"" + img + "\" width=\"50\" height=\"30\">");
                    buf.append("</div>\n");
                    
                    // vertical space
                    buf.append("   <div style=\"width:50px;height:20px;float:left;\"></div>\n");

                    // the time
                    buf.append("   <div style=\"width:50px;height:20px;text-align:center;font-size:18px\">");
                    buf.append(segments[i][j].startTime);
                    buf.append("   </div>\n");
                    
                    // close
                    buf.append("   </div>\n");
                }
            }
            // print a small space
            buf.append("   <div class=\"blockspace\"></div>\n");
        }
        // print the last parking lot
        buf.append("   <div class=\"parking\">");
        buf.append("<img src=\"oneway/parking.png\" width=\"40\" height=\"40\">");
        buf.append("</div>\n");

        buf.append("   <div style=\"clear:both\"> </div>\n");
        buf.append("  </div>\n");
    }

    private void printLights(StringBuffer buf, String dir, boolean[] lights) {
        buf.append("  <div>\n");
        // print a paddling
        if (dir == "left") {
            int spaces = nblocks * 50 + (nblocks+1) * 5 + 40;
            buf.append("   <div style=\"width:" + spaces + "px;height:20px;float:left;\"></div>\n");
        }

        for (int i = 0; i < lights.length; i++) {
            String light = "arrow-" + dir;
            if (lights[i] == true)
                light = light + "-green";
            else
                light = light + "-red";

            // print a small space to align with the parking lot
            buf.append("   <div style=\"width:10px;height:20px;float:left;\"></div>\n");
            buf.append("   <div class=\"" + light + "\"></div>\n");
            // print a small space to align with the parking lot
            buf.append("   <div style=\"width:10px;height:20px;float:left;\"></div>\n");
            // leave space for blocks
            int spaces = nblocks * 50 + (nblocks+1) * 5;
            buf.append("   <div style=\"width:" + spaces + "px;height:20px;float:left;\"></div>\n");
        }
        buf.append("   <div style=\"clear:both\"> </div>\n");
        buf.append("  </div>\n");
    }

    protected void play() throws Exception {
        // create a HTTP Server

		int refresh = 0;
		char req = 'X';
        HTTPServer server = new HTTPServer();
        int port = server.port();
        System.err.println("Port: " + port);
        while ((req = server.nextRequest(0)) == 'I');
        if (req != 'B')
            throw new Exception("Invalid first request");

		for (File f : directoryFiles("oneway/sim/webpages", ".html"))
			f.delete();
		FileOutputStream out = new FileOutputStream("oneway/sim/webpages/index.html");
		out.write(state().getBytes());
		out.close();

        // play the game
		for (tick = 0; tick < MAX_TICKS; ++tick) {
			boolean f = true;
			if (server != null) do {
				if (!f) refresh = 0;
				server.replyState(state(), refresh);
				while (((req = server.nextRequest(0)) == 'I') || req == 'X');
				if (req == 'S') refresh = 0;
				else if (req == 'P') refresh = 1;
				f = false;
			} while (req == 'B');
			
            // Make a copy of current status
            Parking[] lcopy = copyList(left);
            Parking[] rcopy = copyList(right);
            MovingCar[] movingcopy = movingCars.toArray(new MovingCar[0]);
            // Let the player set the lights
            player.setLights(movingcopy, lcopy, rcopy, llights, rlights);

            boolean success = playStep(llights, rlights, tick);
            if (!success)
                break;

            printStep();

            out = new FileOutputStream("oneway/sim/webpages/" + tick + ".html");
			out.write(state().getBytes());
			out.close();

            if (deliveredCars == cars.size())
                break;
		}

        if (cars.size() != deliveredCars) {
            if (errmsg == null)
                errmsg = "Time limit exceeds.";
        }

        // clean up
		if (server != null) {
			server.replyState(state(), 0);
			while ((req = server.nextRequest(2000)) == 'I');
		}
		server.close();
    }


    public OnewayGUI(Player player, String configFilePath, String timingFilePath) {
        super(player, configFilePath, timingFilePath);
    }
}
