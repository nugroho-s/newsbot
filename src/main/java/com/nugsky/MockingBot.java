package com.nugsky;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.EventListener;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.security.auth.login.LoginException;

public class MockingBot implements EventListener {
  public static String RES_FOLDER;
  public static void main(String[] args) {
    // Note: It is important to register your ReadyListener before building
    JDA jda = null;
    try {
      jda = new JDABuilder(args[0])
          .addEventListener(new MockingBot())
          .build();
    } catch (LoginException e) {
      e.printStackTrace();
    }

    // optionally block until JDA is ready
    try {
      jda.awaitReady();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onEvent(Event event) {
    if (event instanceof ReadyEvent) {
      System.out.println("API is ready!");
    } else if (event instanceof MessageReceivedEvent){
      if (((MessageReceivedEvent) event).getMessage().getAuthor().isBot()){
        return;
      }

      //human
      String rawMsg = ((MessageReceivedEvent) event).getMessage().getContentRaw();
      MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) event;
      if (rawMsg.charAt(0) == '#'){
        String command = rawMsg.split(" ",2)[0];
        if (command.equals("#mock")){
          String[] mockMsg = rawMsg.split(" ",2);
          if (mockMsg.length > 1) {
            System.out.println("mocking");
            BufferedImage bufferedImage = null;
            try {
              bufferedImage = ImageIO.read(new URL("https://i.imgur.com/0gDmUzX.jpg"));
              Graphics g = bufferedImage.getGraphics();
              g.setFont(g.getFont().deriveFont(30f));
              g.setColor(Color.BLACK);
              g.drawString(mockMsg[1], 100, 100);
              g.dispose();
              ByteArrayOutputStream os = new ByteArrayOutputStream();
              ImageIO.write(bufferedImage,"png", os);
              InputStream fis = new ByteArrayInputStream(os.toByteArray());
              ((MessageReceivedEvent) event).getChannel().sendFile(fis,"mock.png").queue();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        } else if (command.equals("#ping")) {
          messageReceivedEvent.getChannel().sendMessage("pong").queue();
        }
      }
    }
  }
}
