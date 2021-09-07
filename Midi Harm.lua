
engine.name = 'mHarm'
m = midi.connect(1)
m.event = function(data) 
            tab.print(midi.to_msg(data)) 
          end


m.event = function(data)
  local d = midi.to_msg(data)
  if d.type == "note_on" then
    play_voice(d.note,d.vel)
  elseif d.type == "note_off" then
    clear_voice(d.note)
  end
end

function play_voice(nn,v)
  
  engine.NoteOn(nn,v)
  
end

function clear_voice(nn)
  
  engine.NoteOff(nn)
  
end

function rerun()
norns.script.load(norns.state.script)
end