import vlc
import time

my_song = vlc.MediaPlayer("test_parental.mp4")

my_song.play()

file_status = open("status.txt", "w") 
file_status.write("1")
file_status.close()

file_status = open("status.txt", "r") 
status_new = file_status.read()
status_old = status_new
print "OLD " + status_old
print "NEW " + status_new

while True:
	time.sleep(1)
	file_status = open("status.txt", "r") 
	status_new = file_status.read()
	file_status.close()
	
	if status_new != status_old:
		print "OLD " + status_old
		print "NEW " + status_new
		my_song.pause()
		status_old = status_new
