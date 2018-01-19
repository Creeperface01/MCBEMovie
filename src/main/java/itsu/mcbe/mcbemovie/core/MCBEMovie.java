package itsu.mcbe.mcbemovie.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.scale.AWTUtil;

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

    private File movie;
    
    private FrameGrab grab;

    @Override
    public void onEnable() {
        getLogger().info("起動しました。");
        getServer().getPluginManager().registerEvents(this, this);

        try {
            movie = new File("mcbemovie.mp4");
            grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(movie));
            map = new ItemMap();
            
            map.setImage(AWTUtil.toBufferedImage(FrameGrab.getFrameFromFile(movie, 1)));
        } catch(Exception e){
        	e.printStackTrace();
        }
    }

    private AsyncTask play(Player p) {
            AsyncTask ul = new AsyncTask() {
                @Override
                public void onRun() {
                	
                	p.sendMessage(TextFormat.GREEN + "再生を開始します。: " + movie.getName());
                	
                	//音楽を再生
                	PlaySoundPacket pk = new PlaySoundPacket();
                    pk.x = (int) p.x;
                    pk.y = (int) p.y;
                    pk.z = (int) p.z;
                    pk.name = "music.mcbemovie";
                    pk.volume = 400f;
                    pk.pitch = 1;
                    p.dataPacket(pk);
                    
                    //動画を再生
                    try {
                    	grab = FrameGrab.createFrameGrab(NIOUtils.readableChannel(movie));
                        BufferedImage image;
                        while (null != (image = getFrame())) {
                            Thread.sleep(34);
                            map.setImage(image);
                            p.sendImage(image, map);
                        }
                    } catch (IOException | InterruptedException | JCodecException e) {
                        e.printStackTrace();
                    }

                    //音楽を停止
                    StopSoundPacket pkpk = new StopSoundPacket();
                    pkpk.name = "music.mcbemovie";
                    pkpk.stopAll = true;
                    p.dataPacket(pkpk);
                    
                    this.cleanObject();
                    
                }
            };
            
            return ul;
    }

    @SuppressWarnings("deprecation")
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
                    	getServer().getScheduler().scheduleAsyncTask(play(p));
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

    public BufferedImage getFrame() {
        try {
        	return AWTUtil.toBufferedImage(grab.getNativeFrame());
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }
}
