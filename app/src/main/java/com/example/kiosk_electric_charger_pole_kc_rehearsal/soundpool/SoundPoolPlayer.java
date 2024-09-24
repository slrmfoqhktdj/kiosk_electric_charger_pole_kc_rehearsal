package com.example.kiosk_electric_charger_pole_kc_rehearsal.soundpool;

import android.content.Context;
import android.media.SoundPool;

import com.example.kiosk_electric_charger_pole_kc_rehearsal.R;

import java.util.HashMap;

public class SoundPoolPlayer {
    public static final int PAY_SELECT_FRAGMENT_SOUND = R.raw.pay_select_fragment_sound;
    public static final int INPUT_PAY_FRAGMENT_SOUND = R.raw.input_pay_fragment_sound;
    public static final int CHARGE_INFO_FRAGMENT_SOUND = R.raw.charge_info_fragment_sound;
    public static final int CHARGE_COMPLETE_FRAGMENT_SOUND = R.raw.charge_complete_fragment_sound;

    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;

    private static SoundPoolPlayer soundPoolPlayer;

    public static SoundPoolPlayer getInstance() {
        if (soundPoolPlayer == null) {
            soundPoolPlayer = new SoundPoolPlayer();
        }
        return soundPoolPlayer;
    }

    // sound media initialize
    public void initSounds(Context context) {

        soundPool = new SoundPool.Builder().build();

        soundPoolMap = new HashMap<>();
        soundPoolMap.put(PAY_SELECT_FRAGMENT_SOUND, soundPool.load(context, PAY_SELECT_FRAGMENT_SOUND, 1));
        soundPoolMap.put(INPUT_PAY_FRAGMENT_SOUND, soundPool.load(context, INPUT_PAY_FRAGMENT_SOUND, 1));
        soundPoolMap.put(CHARGE_INFO_FRAGMENT_SOUND, soundPool.load(context, CHARGE_INFO_FRAGMENT_SOUND, 1));
        soundPoolMap.put(CHARGE_COMPLETE_FRAGMENT_SOUND, soundPool.load(context, CHARGE_COMPLETE_FRAGMENT_SOUND, 1));

    }

    public void play(int raw_id){
        if( soundPoolMap.containsKey(raw_id) ) {
            soundPool.play(soundPoolMap.get(raw_id), 1, 1, 1, 0, 1f);
        }
    }

    public void closeSounds(){
        soundPool.release();
        soundPool = null;
    }
}
