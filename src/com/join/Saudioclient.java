package com.join;

/** ��������� thread*/

import java.io.*;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

public class Saudioclient extends Thread { 
	
    protected AudioRecord m_in_rec; // �ŧi��������
    protected int         m_in_buf_size; // �ŧibuffer size��
    
    protected short []      buffer; // �ŧibuffer����
    
    protected boolean     m_keep_running; // �ŧi�O�_���������Ъ���
    protected boolean     thread_running;
    //=======================================
    protected boolean     voice_active;
    protected boolean     voice_S1;
    protected double [] E_max = new double[500];
    protected double [] E_min = new double[500];
    protected double [] E_now = new double[500];
    protected double [] E_thr = new double[500];
    int Nf=0;
    //=======================================
    int sampleRateInHz = 16000; //8000 44100, 22050 and 11025  �ŧi�ظm�����W�v��       
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; // �ŧi�ظmchannel configuration��         
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT; // �ŧi�ظmencoding��
    
	private File SDCardpath; // �ŧiSD card�ؿ�����
	private File myRecAudioFile; // �ŧi�ɮ׸��|����
	private File myRecAudioDir; // �ŧi�ɮש��ݥؿ�����
	
	private boolean sdCardExit;
	
	private TimeMergeResult main;
	private Counttimes time1;
	
	
	public void setMainActivity (TimeMergeResult m){
		
		main = m; // �]�w�n�[��D�{��
		
	}
    
    // �}�l����thread�e����l��method
    public void init() {
    	
    	// �ظm��������buffer size
    	m_in_buf_size =  AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    	
    	// �ظm��������
    	m_in_rec = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRateInHz, channelConfig, audioFormat, m_in_buf_size);
    	
    	
    	// �ظm�C��buffer element��reference array�A�C��element type �� short
    	buffer = new short [m_in_buf_size];
    	
    	
    	// �P�_SD Card�O�_���J
	    sdCardExit = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	    if (sdCardExit){
	    	
	    	SDCardpath = Environment.getExternalStorageDirectory();
	    	myRecAudioDir = new File( SDCardpath.getAbsolutePath() + "/temprecord" );
            
	    	// �Y�S�����ؿ�
            if( !myRecAudioDir.exists() ) {
            	
            	myRecAudioDir.mkdirs();
            	
            }
	    
	    }
    	
    	
    }
    
    // ����thread��method
    public void run() {
    	
    	thread_running = true;
    	
    	android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    	
  	  	myRecAudioFile = new File (SDCardpath.getAbsolutePath() + "/temprecord/test.pcm");
  	  	
  	  	try {
  	  		
  	  		//�p�G���w�g�s�b�A���R��(�л\�쥻���ɮ�)
  	        if (myRecAudioFile.exists()) myRecAudioFile.delete();
  				
  	        // �إ�file
  	        myRecAudioFile.createNewFile();
  	        
  	        
  	        // �إ��ɮ׿�Jstream����
  	        FileOutputStream fisWriter = new FileOutputStream (myRecAudioFile);
    		BufferedOutputStream bos = new BufferedOutputStream(fisWriter);
 	  		DataOutputStream dos = new DataOutputStream(bos);
 	  		
  	  		// �}�l����
  	  		m_in_rec.startRecording();
  	  		
  	  		// �}�l�p��
  	  		time1 = new Counttimes();
  	  		time1.setMainActivity(main);
  	  		time1.setAudiothread(Saudioclient.this);
  	  		time1.start();
  	  		
  	  		// �N�i�}�l���������г]��true
  	    	m_keep_running = true;
  	  		
  	    	// VAD��l��
  	    	voice_active = false;
  	    	voice_S1=true;
  	    	int fc=0;//fc => ������ب�512�I(32ms)
  	    	short[] frame = new short [512];
  	    	E_min[0]=30;
  	    	E_max[0]=0;
  	    	int EPC=0,IR=0;
  	    	Nf=0;
  	    	
  	  		// �N������Ƽg�J�ɮ�
  	  		while(m_keep_running) {

  	  			int len = m_in_rec.read(buffer, 0, m_in_buf_size);
  	  			int i=0;
  	  			
  	  			while(i<len){//buffer while
  	  				
  	  				if(fc<512){//�����@��frame���ɭ��~�������
  	  					
  	  					frame[fc] = buffer[i];
  	  					fc++;
  	  				}
	  				else{
	  					//����n��FFT�\��g�b�o��//
	  					
	  					//frame[511] = buffer[i];
	  					/* �캡�@��frame, ��istate machine�ˬd
	  					 * S0: ��sgloble E_max/min
	  					 * S1: �����y���_�l�I
	  					 * S2: ���I�аO
	  					 * S3: ���ݤU�@�q�y��
	  					 * S4: �����P�w
	  					 * S5: ���I�P�M
	  					 */
	  					Nf++;
	  					E_now[Nf]=E_cal(frame);
	  					E_refresh(E_cal(frame));
	  					E_thr[Nf] = 0.8*E_min[Nf]+(0.2)*(E_max[Nf]-E_min[Nf]);
	  					/* State 0: 
	  					 * ��sgloble E_max/min
	  					 * �ʺA�վ�threshold
	  					 * Nf = Nf+1;
	  					*/

	  					if(voice_S1){
	  						/* �P�_�O�_���y����J
	  						 * �]��voice_S1=true, �L�צp��@�}�l�|���i�o��
	  						 */

	  						// Case.1  �}�l���I�����T
	  						if((E_max[Nf]>5*E_min[Nf])&&(Nf>5)){
	  							voice_S1=false;
	  							for(int j=(Nf-5);j<Nf;j++){
	  								if (E_max[j]<E_max[j+1]){
	  									;
	  									//�p�G�s��5��frame����, ����U�@��state
	  								}
	  								else{
	  									//�u�n���O�s��5��frame������
	  									voice_S1=true;
	  								}
	  							}
	  						}
	  						else{
	  						// Case.2  �}�l���y���T��
	  						}
	  					}
	  					else{
	  						/* voice_S1=false
	  						 * State 2: ��ܻy���}�l�i�J, ���U�ӭn��쵲�����I
	  						 */
	  						if(E_now[Nf]<E_thr[Nf]){
	  							//�y������i�J, �i�JState 3: �P�_�O�_���U�@�q�y��
	  							Log.d("detect", "E_now["+Nf+"]="+E_now[Nf]);
	  							Log.d("detect", "E_thr["+Nf+"]="+E_thr[Nf]);
	  							EPC++;
	  							IR=0;
	  							if(EPC>50){
	  								//��쵲�����I
	  								m_keep_running=false;
	  								time1.interrupt();
	  								Log.d("detect", "Nf="+Nf);
	  								Log.d("detect", "EPC="+EPC);
	  							}
	  						}
	  						else{
	  							//E_now>E_thr, ���y���~��i�J
	  							//State 4: �P�_�O�_�����T�z�Z
	  							if(IR>5){
	  								EPC=0;
	  								IR=0;
	  							}
	  							else{
	  								EPC++;
	  								IR++;
	  							}
	  						}
	  					}
	  					/* �~�����U�@��frame
	  					 * frame shift = 10ms => ���L160�I
	  					 * �ƻs512-160=352�I
	  					 */
	  					
	  					fc=352; 
	  					for(int j=0;j<352;j++){
	  						frame[j]=frame[160+j];
	  					}

	  				}//if-else
  	  				
  	  				
	  				Log.d("message", "Number "+i+": "+String.valueOf(buffer[i]));
	  				// �g�J, len���G��512
	  				dos.writeShort(buffer[i]);
	  				i++;
	  			}
  	  		}//end of while
  	  		
  	  		for(int ii=0;ii<Nf;ii++){
  	  			Log.d("E_now", String.valueOf(E_now[ii]));
  	  			Log.d("E_max", String.valueOf(E_max[ii]));
  	  			Log.d("E_min", String.valueOf(E_min[ii]));
  	  			Log.d("E_thr", String.valueOf(E_thr[ii]));
  	  		}
  	  			  		
  	  		// ������������
  	  		m_in_rec.stop();
  	  		main.bulidsockethandler();
  	  		// �����g�J��ƪ�����
  	  		dos.flush();
  	  		dos.close();
  	  		
  	  		thread_running = false;

  	  	}
  	  	catch (FileNotFoundException e1) {
  	  		e1.printStackTrace();
  	  	} 
  	  	catch (IOException e) {
  	  		e.printStackTrace();
  	  	}
  
    }
    
    // ��������thread��method
    public void free() {
    	m_keep_running = false;
    }
    
    public boolean getrunning(){
    	
    	return thread_running;
    }
    
    public File getfile(){
    	
    	return myRecAudioFile;
    }
    
    public double E_cal(short[] frame){
    	double E=0;
    	for(int i=0;i<512;i++){
    		E = E+((double)frame[i])*((double)frame[i]);
    	}
    	return E/100000000;    	
    }
    
    public void E_refresh(double E){
    	if (E<E_min[Nf-1]){
    		E_min[Nf]=0.5*(E+E_min[Nf-1]);
    	}
    	else{
    		E_min[Nf]=E_min[Nf-1];
    	}
    	if (E>E_max[Nf-1]){
    		E_max[Nf]=0.5*(E+E_max[Nf-1]);
    	}
    	else{
    		E_max[Nf]=E_max[Nf-1];
    	}
    }
    
}  
