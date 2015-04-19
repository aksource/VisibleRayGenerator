package VRGenerator;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy extends CommonProxy
{
	public World getClientWorld()
	{
		return Minecraft.getMinecraft().theWorld;
	}
}