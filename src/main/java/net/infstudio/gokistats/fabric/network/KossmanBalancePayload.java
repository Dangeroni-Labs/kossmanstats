package net.infstudio.gokistats.fabric.network;

import java.util.LinkedHashMap;
import java.util.Map;
import net.infstudio.gokistats.KossmanStats;
import net.infstudio.gokistats.core.config.KossmanBalanceTuning;
import net.infstudio.gokistats.core.config.KossmanDeathPenaltyTuning;
import net.infstudio.gokistats.core.config.KossmanStatTuning;
import net.infstudio.gokistats.core.config.KossmanXpTuning;
import net.infstudio.gokistats.core.definition.KossmanStatDefinitions;
import net.infstudio.gokistats.core.definition.StatDefinition;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record KossmanBalancePayload(KossmanBalanceTuning tuning) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<KossmanBalancePayload> ID = new Type<>(
			Identifier.fromNamespaceAndPath(KossmanStats.MOD_ID, "balance_tuning")
	);
	public static final StreamCodec<RegistryFriendlyByteBuf, KossmanBalancePayload> CODEC =
			CustomPacketPayload.codec(KossmanBalancePayload::write, KossmanBalancePayload::read);

	private static KossmanBalancePayload read(RegistryFriendlyByteBuf buf) {
		int maxStatLevel = Math.max(1, buf.readVarInt());
		KossmanXpTuning xp = new KossmanXpTuning(
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf),
				readNonNegativeDouble(buf)
		);
		double downgradeRefundRate = clamp(buf.readDouble(), 0.0D, 1.0D);
		KossmanDeathPenaltyTuning defaults = KossmanBalanceTuning.DEFAULT.deathPenalty();
		double lossRate = clamp(buf.readDouble(), 0.0D, 1.0D);
		int minimumLoss = Math.max(0, buf.readVarInt());
		int maximumLoss = Math.max(1, buf.readVarInt());
		int minimumRetainedStatLevel = Math.max(0, buf.readVarInt());
		if (maximumLoss < minimumLoss) {
			minimumLoss = defaults.minimumLoss();
			maximumLoss = defaults.maximumLoss();
		}
		KossmanDeathPenaltyTuning deathPenalty = new KossmanDeathPenaltyTuning(
				lossRate,
				minimumLoss,
				maximumLoss,
				minimumRetainedStatLevel
		);

		Map<String, KossmanStatTuning> stats = new LinkedHashMap<>();
		int statCount = buf.readVarInt();
		for (int index = 0; index < statCount; index++) {
			String commandName = buf.readUtf(64);
			boolean enabled = buf.readBoolean();
			double effectMultiplier = readNonNegativeDouble(buf);
			boolean perkEnabled = buf.readBoolean();
			int perkUnlockLevel = Math.max(0, buf.readVarInt());
			stats.put(commandName, new KossmanStatTuning(enabled, effectMultiplier, perkEnabled, perkUnlockLevel));
		}

		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			stats.putIfAbsent(stat.commandName(), KossmanStatTuning.defaultsFor(stat));
		}

		return new KossmanBalancePayload(new KossmanBalanceTuning(
				maxStatLevel,
				xp,
				downgradeRefundRate,
				deathPenalty,
				stats
		));
	}

	private void write(RegistryFriendlyByteBuf buf) {
		buf.writeVarInt(Math.max(1, tuning.maxStatLevel()));
		writeNonNegativeDouble(buf, tuning.xp().costMultiplier());
		writeNonNegativeDouble(buf, tuning.xp().baseOffset());
		writeNonNegativeDouble(buf, tuning.xp().linearScale());
		writeNonNegativeDouble(buf, tuning.xp().quadraticScale());
		writeNonNegativeDouble(buf, tuning.xp().cubicScale());
		writeNonNegativeDouble(buf, tuning.xp().midgameRampStart());
		writeNonNegativeDouble(buf, tuning.xp().midgameRampScale());
		writeNonNegativeDouble(buf, tuning.xp().lategameRampStart());
		writeNonNegativeDouble(buf, tuning.xp().lategameRampScale());
		writeNonNegativeDouble(buf, tuning.xp().endgameRampStart());
		writeNonNegativeDouble(buf, tuning.xp().endgameRampScale());
		buf.writeDouble(clamp(tuning.downgradeRefundRate(), 0.0D, 1.0D));
		buf.writeDouble(clamp(tuning.deathPenalty().lossRate(), 0.0D, 1.0D));
		buf.writeVarInt(Math.max(0, tuning.deathPenalty().minimumLoss()));
		buf.writeVarInt(Math.max(1, tuning.deathPenalty().maximumLoss()));
		buf.writeVarInt(Math.max(0, tuning.deathPenalty().minimumRetainedStatLevel()));
		buf.writeVarInt(KossmanStatDefinitions.ALL.size());
		for (StatDefinition stat : KossmanStatDefinitions.ALL) {
			KossmanStatTuning statTuning = tuning.stat(stat);
			buf.writeUtf(stat.commandName(), 64);
			buf.writeBoolean(statTuning.enabled());
			writeNonNegativeDouble(buf, statTuning.effectMultiplier());
			buf.writeBoolean(statTuning.perkEnabled());
			buf.writeVarInt(Math.max(0, statTuning.perkUnlockLevel()));
		}
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return ID;
	}

	private static double readNonNegativeDouble(RegistryFriendlyByteBuf buf) {
		return Math.max(0.0D, buf.readDouble());
	}

	private static void writeNonNegativeDouble(RegistryFriendlyByteBuf buf, double value) {
		buf.writeDouble(Math.max(0.0D, value));
	}

	private static double clamp(double value, double min, double max) {
		return Math.max(min, Math.min(max, value));
	}
}
