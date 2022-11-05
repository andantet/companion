package dev.andante.mccic.api.game;

public interface Games {
    HoleInTheWallGame HOLE_IN_THE_WALL = register("hole_in_the_wall", new HoleInTheWallGame());
    TGTTOSGame TGTTOS = register("tgttos", new TGTTOSGame());
    SkyBattleGame SKY_BATTLE = register("sky_battle", new SkyBattleGame());
    BattleBoxGame BATTLE_BOX = register("battle_box", new BattleBoxGame());

    private static <T extends Game> T register(String id, T game) {
        return GameRegistry.INSTANCE.register(id, game);
    }
}
