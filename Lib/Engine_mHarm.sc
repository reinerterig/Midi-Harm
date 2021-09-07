Engine_mHarm : CroneEngine {

  var <synth;

  *new { arg context, doneCallback;
    ^super.new(context, doneCallback);
  }


  alloc {

		//test.poll(0.5, label: \Test: );
SynthDef(\TunePA, {|inL, inR, out, amp=1, gate = 1, note = 60, vel = 127|
			var midiDiff, harmonize, confidence, inPitch, sig, env;
			var in = [In.ar(inL), In.ar(inR)]; //live input
			gate.poll(0.5, label: \gate);
			note.poll(0.5, label: \note);
			 // pitch tracking
    #inPitch, confidence = Pitch.kr(in); //pitch tracker included in SC

	inPitch.poll(0.5, label: \pitchHz);
	//inPitch.cpsmidi.poll(0.5, label: \pitchNote);

    //get the difference
	midiDiff = note - inPitch.cpsmidi; // get difference between desired note and current note
	//midiDiff.poll(0.5, label: \midiDiff);

    sig = Mix(
        PitchShiftPA.ar(
            in,
            inPitch, //pitch tracking - we take just the frequency
            midiDiff.midiratio, //pitchRatio
            1, //formantRatio
			grainsPeriod: 0.5,
        )
    );

	env = EnvGen.kr(Env.adsr(releaseTime: 1), gate, doneAction: 2);
	//amp = vel / 127;
	sig = sig * env * amp;

    //output
    // [sig, in]; //listen to both signals
    
    sig = Pan2.ar(sig,0) * 10  ;
    Out.ar(out,sig);
    }).add;

    context.server.sync;
/*
    synth = Synth.new(\ReTune, [
      \inL, context.in_b[0].index,
      \inR, context.in_b[1].index,
      \out, context.out_b.index,
      \amp, 1],
    context.xg);
*/
// commands

~aNotes = Array.newClear(128);

		// engine.NoteOn(nn,vel)
	this.addCommand("NoteOn", "if", {|msg|
			~aNotes[msg[1]] = Synth.new(\TunePA, [
      \inL, context.in_b[0].index,
      \inR, context.in_b[1].index,
	  \gate, 1,
	  \note, msg[1],
	  \vel, msg[2],
      \out, context.out_b.index
      ],
    context.xg);
	//	~aNotes[msg[1]].set(\gate, 1,\note[1],\vel, msg[2]);

    });
		// engine.NoteOff(nn)
	this.addCommand("NoteOff", "i", {|msg|
      ~aNotes[msg[1]].set(\gate, 0);
	  ~aNotes[msg[1]] = nil;
    });

/*
	this.addCommand("gate", "i", {|msg|
      synth.set(\gate, msg[1]);
    });

	this.addCommand("vel", "f", {|msg|
      synth.set(\vel, msg[1]);
    });
	this.addCommand("amp", "f", {|msg|
      synth.set(\amp, msg[1]);
    });
*/
  }

  free {
             // here you should free resources (e.g. Synths, Buffers &c)
// and stop processes (e.g. Routines, Tasks &c)
            synth.free;
  }

}
