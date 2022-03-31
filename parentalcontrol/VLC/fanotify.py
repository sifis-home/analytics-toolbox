from __future__ import print_function
import os
import sys

import fanotify


def main():
  if len(sys.argv) != 2:
    print('Usage: {} <path>'.format(sys.argv[0]))
    sys.exit(1)

  fan_fd = fanotify.Init(fanotify.FAN_CLASS_CONTENT, os.O_RDONLY)
  fanotify.Mark(fan_fd,
		fanotify.FAN_MARK_ADD | fanotify.FAN_MARK_MOUNT,
                fanotify.FAN_OPEN | fanotify.FAN_EVENT_ON_CHILD,
                -1,
                sys.argv[1])

  while True:
    buf = os.read(fan_fd, 4096)
    assert buf
    while fanotify.EventOk(buf):
      buf, event = fanotify.EventNext(buf)
      if event.mask & fanotify.FAN_Q_OVERFLOW:
        print('Queue overflow !')
        continue
      fdpath = '/proc/self/fd/{:d}'.format(event.fd)
      full_path = os.readlink(fdpath)
      print(full_path)
    assert not buf

if __name__ == '__main__':
	main()
