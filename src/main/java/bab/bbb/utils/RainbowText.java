package bab.bbb.utils;

import java.util.Arrays;
import java.util.List;
 
public class RainbowText {
	private String text = "";
	private String fancyText = "";
	private static final List<String> RAINBOW = Arrays.asList("§4", "§c", "§6", "§e", "§a", "§2", "§b", "§3" /**/, "§9", "§1",/**/ "§5", "§d"); // size is 12
	private List<String> rainbowArray;
	private String prefix = "";
	public RainbowText(String text)
	{
		if(text != null){
			this.text = text;
		}
		rainbowArray = RAINBOW;
		updateFancy();
	}

	private void updateFancy(){
		int spot = 0;
		String fancyText = "";
		for(char l : text.toCharArray()){
			String letter = Character.toString(l);
			String t1 = fancyText;
			if(!letter.equalsIgnoreCase(" ")){
				fancyText = t1 + rainbowArray.get(spot) + prefix + letter;
				if(spot == rainbowArray.size() - 1){
					spot = 0;
				} else {
					spot++;
				}
			} else {
				fancyText = t1 + letter;
			}
		}
		this.fancyText = fancyText;
	}

	public String getText(){
		return this.fancyText;
	}
}