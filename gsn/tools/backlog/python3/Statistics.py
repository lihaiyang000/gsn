
__author__      = "Tonio Gsell <tgsell@tik.ee.ethz.ch>"
__copyright__   = "Copyright 2010, ETH Zurich, Switzerland, Tonio Gsell"
__license__     = "GPL"
__version__     = "$Revision: 2381 $"
__date__        = "$Date: 2010-11-15 14:51:38 +0100 (Mon, 15. Nov 2010) $"
__id__          = "$Id: AbstractPlugin.py 2381 2010-11-15 13:51:38Z tgsell $"
__source__      = "$URL: https://gsn.svn.sourceforge.net/svnroot/gsn/branches/permasense/gsn/tools/backlog/python/AbstractPlugin.py $"

import time
from threading import Lock, Thread



class StatisticsClass:
    '''
    This class can be used to get statistics like packet count per minute.
    
    '''

    '''
    data/instance attributes:
    _logger
    _outCounter
    _outCounterLock
    _inCounter
    _inCounterLock
    _timer
    _timerLock
    '''

    def __init__(self, parent):
        self._outCounter = 0
        self._outCounterLock = Lock()
        self._inCounter = 0
        self._inCounterLock = Lock()
        
        self._timers = dict()
        
        
    def outgoingMessageAction(self):
        self._outCounterLock.acquire()
        self._outCounter += 1
        self._outCounterLock.release()
        
        
    def incomingMessageAction(self):
        self._inCounterLock.acquire()
        self._inCounter += 1
        self._inCounterLock.release()
        
        
    def getIncomingActionsPerMinute(self):
        
        
        
    def startNewTimer(self):
        uniqueId = uuid.uuid4()
        self._timers.update({uniqueId: timer.time()})
        return uniqueId
        
        
    def getTimerDiff(self, uniqueId):
        try:
            return time.time() - self._timers.pop(uniqueId)
        except KeyError:
            raise
        