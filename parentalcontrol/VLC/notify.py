import pyinotify
import os.path
import subprocess
import os


class MyEventHandler(pyinotify.ProcessEvent):

    '''
    def process_IN_ACCESS(self, event):
        #print "ACCESS event:", event.pathname
	path_name = event.pathname
	if is_video_file(path_name) == True:
		print "ACCESS: "
		#PID = os.system("fuser " + path_name)
		proc = subprocess.Popen(['fuser', path_name], stdout=subprocess.PIPE)	
		output = proc.communicate()[0]
		PID = str(output)		
		
		

		pid_presents = False
		if os.path.exists("PID_opened.txt"): 
			with open("PID_opened.txt", "r") as ins:
				for line in ins:
					print "FILE: " + line
					if str(PID) in line:
						print "PID presents"
						pid_presents = True
						break
		if pid_presents == False:
			file = open("PID_opened.txt","a") 
			file.write(path_name + " " + str(PID) + "\n")
			file.close() 
    '''		

    def process_IN_CLOSE_NOWRITE(self, event):
        #print "CLOSE_NOWRITE event:", event.pathname
	path_name = event.pathname
	if is_video_file(path_name) == True:
		print "CLOSE: " + path_name
		
		with open('PID_opened.txt', 'r+') as f:
    			t = f.read()
    			f.seek(0)
    			for line in t.split('\n'):
				print "LINE " + line
				print "PATH_NAME " + path_name 
        			if path_name not in line:
            				f.write(line + '\n')
    					f.truncate()
   
    def process_IN_OPEN(self, event):
        #print "ACCESS event:", event.pathname
	path_name = event.pathname
	if is_video_file(path_name) == True:
		print "OPEN: "
		#PID = os.system("fuser " + path_name)
		proc = subprocess.Popen(['fuser', path_name], stdout=subprocess.PIPE)	
		output = proc.communicate()[0]
		PID = str(output)		
		
		

		pid_presents = False
		if os.path.exists("PID_opened.txt"): 
			with open("PID_opened.txt", "r") as ins:
				for line in ins:
					print "FILE: " + line
					if str(PID) in line:
						print "PID presents"
						pid_presents = True
						break
		if pid_presents == False:
			file = open("PID_opened.txt","a") 
			file.write(path_name + ": " + str(PID))
			file.close() 
		

'''
    def process_IN_CLOSE_WRITE(self, event):
        print "CLOSE_WRITE event:", event.pathname

    def process_IN_ATTRIB(self, event):
        print "ATTRIB event:", event.pathname

    def process_IN_CREATE(self, event):
        print "CREATE event:", event.pathname

    def process_IN_DELETE(self, event):
        print "DELETE event:", event.pathname

    def process_IN_MODIFY(self, event):
        print "MODIFY event:", event.pathname
'''   



def is_video_file(filename):
	video_file_extensions = (
	'.264', '.3g2', '.3gp', '.3gp2', '.3gpp', '.3gpp2', '.3mm', '.3p2', '.60d', '.787', '.89', '.aaf', '.aec', '.aep', '.aepx',
	'.aet', '.aetx', '.ajp', '.ale', '.am', '.amc', '.amv', '.amx', '.anim', '.aqt', '.arcut', '.arf', '.asf', '.asx', '.avb',
	'.avc', '.avd', '.avi', '.avp', '.avs', '.avs', '.avv', '.axm', '.bdm', '.bdmv', '.bdt2', '.bdt3', '.bik', '.bin', '.bix',
	'.bmk', '.bnp', '.box', '.bs4', '.bsf', '.bvr', '.byu', '.camproj', '.camrec', '.camv', '.ced', '.cel', '.cine', '.cip',
	'.clpi', '.cmmp', '.cmmtpl', '.cmproj', '.cmrec', '.cpi', '.cst', '.cvc', '.cx3', '.d2v', '.d3v', '.dat', '.dav', '.dce',
	'.dck', '.dcr', '.dcr', '.ddat', '.dif', '.divx', '.dlx', '.dmb', '.dmsd', '.dmsd3d', '.dmsm', '.dmsm3d', '.dmss',
	'.dmx', '.dnc', '.dpa', '.dpg', '.dream', '.dsy', '.dv', '.dv-avi', '.dv4', '.dvdmedia', '.dvr', '.dvr-ms', '.dvx', '.dxr',
	'.dzm', '.dzp', '.dzt', '.edl', '.evo', '.eye', '.ezt', '.f4p', '.f4v', '.fbr', '.fbr', '.fbz', '.fcp', '.fcproject',
	'.ffd', '.flc', '.flh', '.fli', '.flv', '.flx', '.gfp', '.gl', '.gom', '.grasp', '.gts', '.gvi', '.gvp', '.h264', '.hdmov',
	'.hkm', '.ifo', '.imovieproj', '.imovieproject', '.ircp', '.irf', '.ism', '.ismc', '.ismv', '.iva', '.ivf', '.ivr', '.ivs',
	'.izz', '.izzy', '.jss', '.jts', '.jtv', '.k3g', '.kmv', '.ktn', '.lrec', '.lsf', '.lsx', '.m15', '.m1pg', '.m1v', '.m21',
	'.m21', '.m2a', '.m2p', '.m2t', '.m2ts', '.m2v', '.m4e', '.m4u', '.m4v', '.m75', '.mani', '.meta', '.mgv', '.mj2', '.mjp',
	'.mjpg', '.mk3d', '.mkv', '.mmv', '.mnv', '.mob', '.mod', '.modd', '.moff', '.moi', '.moov', '.mov', '.movie', '.mp21',
	'.mp21', '.mp2v', '.mp4', '.mp4v', '.mpe', '.mpeg', '.mpeg1', '.mpeg4', '.mpf', '.mpg', '.mpg2', '.mpgindex', '.mpl',
	'.mpl', '.mpls', '.mpsub', '.mpv', '.mpv2', '.mqv', '.msdvd', '.mse', '.msh', '.mswmm', '.mts', '.mtv', '.mvb', '.mvc',
	'.mvd', '.mve', '.mvex', '.mvp', '.mvp', '.mvy', '.mxf', '.mxv', '.mys', '.ncor', '.nsv', '.nut', '.nuv', '.nvc', '.ogm',
	'.ogv', '.ogx', '.osp', '.otrkey', '.pac', '.par', '.pds', '.pgi', '.photoshow', '.piv', '.pjs', '.playlist', '.plproj',
	'.pmf', '.pmv', '.pns', '.ppj', '.prel', '.pro', '.prproj', '.prtl', '.psb', '.psh', '.pssd', '.pva', '.pvr', '.pxv',
	'.qt', '.qtch', '.qtindex', '.qtl', '.qtm', '.qtz', '.r3d', '.rcd', '.rcproject', '.rdb', '.rec', '.rm', '.rmd', '.rmd',
	'.rmp', '.rms', '.rmv', '.rmvb', '.roq', '.rp', '.rsx', '.rts', '.rts', '.rum', '.rv', '.rvid', '.rvl', '.sbk', '.sbt',
	'.scc', '.scm', '.scm', '.scn', '.screenflow', '.sec', '.sedprj', '.seq', '.sfd', '.sfvidcap', '.siv', '.smi', '.smi',
	'.smil', '.smk', '.sml', '.smv', '.spl', '.sqz', '.srt', '.ssf', '.ssm', '.stl', '.str', '.stx', '.svi', '.swf', '.swi',
	'.swt', '.tda3mt', '.tdx', '.thp', '.tivo', '.tix', '.tod', '.tp', '.tp0', '.tpd', '.tpr', '.trp', '.tsp', '.ttxt',
	'.tvs', '.usf', '.usm', '.vc1', '.vcpf', '.vcr', '.vcv', '.vdo', '.vdr', '.vdx', '.veg','.vem', '.vep', '.vf', '.vft',
	'.vfw', '.vfz', '.vgz', '.vid', '.video', '.viewlet', '.viv', '.vivo', '.vlab', '.vob', '.vp3', '.vp6', '.vp7', '.vpj',
	'.vro', '.vs4', '.vse', '.vsp', '.w32', '.wcp', '.webm', '.wlmp', '.wm', '.wmd', '.wmmp', '.wmv', '.wmx', '.wot', '.wp3',
	'.wpl', '.wtv', '.wve', '.wvx', '.xej', '.xel', '.xesc', '.xfl', '.xlmv', '.xmv', '.xvid', '.y4m', '.yog', '.yuv', '.zeg',
	'.zm1', '.zm2', '.zm3', '.zmv'  )

	if filename.endswith((video_file_extensions)):
    		return True

def main():
    # watch manager
    wm = pyinotify.WatchManager()
    wm.add_watch('/home/', pyinotify.ALL_EVENTS, rec=True)

    # event handler
    eh = MyEventHandler()

    # notifier
    notifier = pyinotify.Notifier(wm, eh)
    notifier.loop()

if __name__ == '__main__':
    main()
