package itsu.mcbe.mcbemovie.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.event.Listener;
import cn.nukkit.item.ItemMap;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.network.protocol.StopSoundPacket;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;

public class MCBEMovie extends PluginBase implements Listener {
	
	private ItemMap map;
	
	private Iterator<ImageReader> itr = ImageIO.getImageReadersByFormatName("gif");
	private ImageReader reader = null;
	private List<BufferedImage> images = new ArrayList<>();

	@Override
	public void onEnable() {
		getLogger().info("起動しました。");
		getServer().getPluginManager().registerEvents(this, this);
		
		try {
			if(itr.hasNext()) reader = itr.next();
	        else throw new RuntimeException();
			
			reader.setInput(ImageIO.createImageInputStream(new File("mcbemovie.gif")));
			int count = reader.getNumImages(true);
			
			for(int i = 0;i < count;i++) {
	        	images.add(reader.read(i));
	        }
			
			map = new ItemMap();
			map.setImage(images.get(0));
		} catch(Exception ex){}
	}
	
	@SuppressWarnings("deprecation")
	private void play(Player p) {
			AsyncTask ul = new AsyncTask() {
				@Override
				public void onRun() {
				    try {
					    for(BufferedImage img : images) {
					    	map.setImage(img);
							map.sendImage(p);
							Thread.sleep(50);
					    }
					    
					    StopSoundPacket pk = new StopSoundPacket();
						pk.name = "music.mcbemovie";
						pk.stopAll = true;
						p.dataPacket(pk);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			};
			
			AsyncTask music = new AsyncTask() {
				@Override
				public void onRun() {
					PlaySoundPacket pk = new PlaySoundPacket();
					pk.x = (int) p.x;
					pk.y = (int) p.y;
					pk.z = (int) p.z;
					pk.name = "music.mcbemovie";
					pk.volume = 400f;
					pk.pitch = 1;
					p.dataPacket(pk);
				}
			};
			
			p.sendMessage("再生しました。");
			getServer().getScheduler().scheduleAsyncTask(music);
			getServer().getScheduler().scheduleAsyncTask(ul);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equals("movie")) {
			if(sender instanceof ConsoleCommandSender) {
				sender.sendMessage(TextFormat.RED + "ゲーム内で実行してください。");
				return true;
			}
			
			Player p = (Player) sender;
			
			try {
				switch(args[0]) {
				
					case "giveme":
						p.getInventory().addItem(map);
						p.sendMessage(TextFormat.GREEN + "再生用マップを渡しました。");
						break;
						
					case "play":
						play(p);
						break;
						
					default:
						sender.sendMessage(TextFormat.RED + "正しい引数を入力してください。<giveme | play>");
						return true;
						
				}
			} catch(Exception e) {
				sender.sendMessage(TextFormat.RED + "引数を入力してください。<giveme | play>");
				return true;
			}
		}
		return true;
		
	}
}
