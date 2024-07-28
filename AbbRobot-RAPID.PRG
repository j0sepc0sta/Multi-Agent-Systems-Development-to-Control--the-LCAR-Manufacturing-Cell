%%%
  VERSION:1
  LANGUAGE:ENGLISH
%%%

MODULE TESTE
CONST robtarget P0:= [[-608,585.05,882.15],[0.02426,-0.004067,0.999685,0.004989],[1,-1,1,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]];
CONST robtarget P1:= [[-627.63,599.41,875.62],[0.017337,-0.998051,-0.053182,0.027691],[1,0,-1,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]];
CONST robtarget P_C:= [[892.04,18.43,1240.94],[0.007718,0.714377,-0.699546,0.015548],[0,-1,-2,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]];
CONST robtarget P_Buffer_E:= [[343.81,830.01,847.79],[0.040588,-0.023894,0.666967,0.743598],[0,-1,1,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]];
CONST robtarget P_TESTE:= [[-64.09,-681.91,884.04],[0.006538,-0.044898,0.998691,-0.023626],[-2,0,-2,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]];


VAR num PA_OCUPADO:= 0;
VAR num PB_OCUPADO:= 0;
VAR num IA_OCUPADO:= 0;
VAR num IB_OCUPADO:= 0;
VAR num PN_OCUPADO:= 0;
VAR num RFID_OCUPADO:= 0;
Var num MESA_OCUPADO:= 0;
Var num Buffer_OCUPADO:= 0;

VAR num RFID_A:= 0;
VAR num RFID_B:= 0;
VAR num ABERTA:= 0;
Var num Buffer:= 0;


VAR num RFID_LEITURA;

VAR intnum Barreira;
PROC server()
        VAR socketdev server_socket;
        !VAR socketdev client_socket;
        VAR string receive_string;
        VAR string client_ip;
        SocketClose server_socket;
        !SocketClose client_socket;
        SocketCreate server_socket;
        SocketConnect server_socket, "192.168.1.54", 1025;
        SocketListen server_socket;
        SocketAccept server_socket,
        !client_socket\ClientAddress:=client_ip;
        ERROR
        IF ERRNO=ERR_SOCK_TIMEOUT THEN
        RETRY;
        ELSEIF ERRNO=ERR_SOCK_CLOSED THEN
        RETURN;
        ELSE
        ! No error recovery handling
        ENDIF
		
ENDPROC
	
  PROC main()
	SingArea\Wrist;
	TPErase;
	TPWrite "CELULA";
	VAR socketdev server_socket;
    !VAR socketdev client_socket;
    VAR string receive_string;
    VAR string client_ip;
	IF DI10_7=1 THEN
		PA;
		IA_E;
	ENDIF
	IF DI10_9=1 THEN
		IA;
		!IF MESA_OCUPADO = 0 THEN
		!	IF Buffer_OCUPADO = 0 THEN
				Verifica;
		!	ELSE
		!		Buffer_E;
		!	ENDIF
		!ELSE
		!	Buffer_E;
		!ENDIF
	ENDIF
	IF DI10_6=1 THEN
		PB;
		IB_E;
	ENDIF
	IF DI10_10=1 THEN
		IB;
		IF MESA_OCUPADO = 0 THEN
			!IF Buffer_OCUPADO = 0 THEN
				Verifica;
			!ELSE
			!	Buffer_E;
			!ENDIF
		!ELSE
		!	Buffer_E;
		ENDIF
	ENDIF
  ENDPROC
  
  PROC INICIO()
	IF ABERTA = 0 THEN
		abre_garra;
	ENDIF
	MoveL Offs(P1,417.37,260.42,243.85),v300,z10,tool0;
	MoveL Offs(P1,417.37,260.42,105),v200,z10,tool0;
	MoveL Offs(P1,417.37,260.42,28),v30,fine,tool0;
		fecha_garra;
	MoveL Offs(P1,417.37,260.42,243.85),v300,z10,tool0;
  ENDPROC
  
  PROC RFID()
  	MoveL Offs(P1,300.89,95.54,180),v300,z50,tool0;
	MoveL Offs(P1,300.89,95.54,100),v200,z50,tool0;
	MoveL Offs(P1,300.89,95.44,46.34),v30,fine,tool0;
	TPReadFK RFID_LEITURA, "Qual o tipo de peça", "A","B", stEmpty, stEmpty, stEmpty;
	IF RFID_LEITURA = 1 THEN
		RFID_A:= 1;
	ELSEIF RFID_LEITURA = 2 THEN
		RFID_B:= 1;
	ELSE
		TPWrite " Escolha uma opcao valida";
	ENDIF    
  ENDPROC
  
  PROC TESTE_PA()
	IF PA_OCUPADO = 0 THEN
		MoveL Offs(P1,306.89,90.54,180),v300,z50,tool0;
		RFID_OCUPADO:= 0;
		PECA_A;	
	ELSE
		RFID_OCUPADO:= 1;
			abre_garra;
		MoveL Offs(P1,306.89,90.54,180),v200,z50,tool0;
		TPWrite "Punching A ocupado";
	ENDIF	
  ENDPROC
  
  PROC TESTE_PB()
	IF PB_OCUPADO = 0 THEN
		MoveL Offs(P1,306.89,90.54,180),v300,z50,tool0;
		RFID_OCUPADO:= 0;
		PECA_B;	
	ELSE
		RFID_OCUPADO:= 1;
			abre_garra;
		MoveL Offs(P1,306.89,90.54,180),v200,z50,tool0;
		TPWrite "Punching B ocupado";
	ENDIF	
  ENDPROC
 
  PROC PECA_A()
	MoveL Offs(P0,164.79,257.59,250),v300,z50,tool0;
	MoveL Offs(P0,164.79,257.59,100),v200,z50,tool0;
	MoveL Offs(P0,164.79,257.59,60),v30,fine,tool0;
		abre_garra;
	MoveL Offs(P0,164.79,257.59,100),v200,z50,tool0;
	MoveL Offs(P0,164.79,257.59,250),v300,z50,tool0;
	PA_OCUPADO:= 1;
	RFID_A:= 0;
  ENDPROC
  
  PROC PECA_B()
  	MoveL Offs(P0,229.25,456.28,250),v300,z50,tool0;
	MoveL Offs(P0,229.25,456.28,100),v200,z50,tool0;
	MoveL Offs(P0,229.25,456.28,60),v30,fine,tool0;
		abre_garra;
	MoveL Offs(P0,229.25,456.28,100),v200,z50,tool0;
	MoveL Offs(P0,229.25,456.28,250),v300,z50,tool0;
	PB_OCUPADO:=1;
	RFID_B:= 0;
  ENDPROC
 
  PROC PA()
	MoveL Offs(P0,164.79,242,250),v300,z50,tool0;
	MoveL Offs(P0,164.79,242,100),v200,z50,tool0;
	MoveL Offs(P0,164.79,242,51.38),v30,fine,tool0;
	fecha_garra;
	MoveL Offs(P0,164.79,242,100),v200,z50,tool0;
	MoveL Offs(P0,164.79,242,250),v300,z50,tool0;
	PA_OCUPADO:= 0;
  ENDPROC
  
PROC PB()
	MoveL Offs(P0,229.25,456.28,250),v300,z50,tool0;
	MoveL Offs(P0,229.25,456.28,100),v200,z50,tool0;
	MoveL Offs(P0,229.25,456.28,51.73),v30,fine,tool0;
		fecha_garra;
	MoveL Offs(P0,229.25,456.28,100),v200,z50,tool0;
	MoveL Offs(P0,229.25,456.28,250),v300,z50,tool0;
	PB_OCUPADO:= 0;
  ENDPROC

  PROC IA()
	MoveL Offs(P0,523.09,160,250),v300,z50,tool0;
	MoveL Offs(P0,523.09,160,100),v200,z50,tool0;
	MoveL Offs(P0,523.09,160,55.28),v30,fine,tool0;
		fecha_garra;
	MoveL Offs(P0,523.09,170,100),v200,z50,tool0;
	MoveL Offs(P0,523.09,170,250),v300,z50,tool0;
	IA_OCUPADO:= 0;
  ENDPROC

  PROC IB()
	MoveL Offs(P0,795.32,465,250),v300,z50,tool0;
	MoveL Offs(P0,795.32,465,100),v200,z50,tool0;
	MoveL Offs(P0,795.32,465,55.22),v30,fine,tool0;
		fecha_garra;
	MoveL Offs(P0,795.32,465,100),v200,z50,tool0;
	MoveL Offs(P0,795.32,465,250),v300,z50,tool0;
	IB_OCUPADO:= 0;
  ENDPROC
  
  PROC PN()
  !Atencao os pontos nao correspondem a realidade
	MoveL Offs(P0,587,-131,300),v300,z50,tool0;
	MoveL Offs(P0,587,-131,65),v200,z50,tool0;
	MoveL Offs(P0,587,-131,45),v30,fine,tool0;
		fecha_garra;
	MoveL Offs(P0,587,-131,118),v200,z50,tool0;
	MoveL Offs(P0,587,-131,300),v300,z50,tool0;
	PN_OCUPADO:= 0;
  ENDPROC
 
 PROC IA_E()
	MoveL Offs(P0,804.39,165,250),v300,z50,tool0;
	MoveL Offs(P0,804.39,165,100),v200,z50,tool0;
	MoveL Offs(P0,804.39,165,56.09),v30,fine,tool0;
		abre_garra;
	MoveL Offs(P0,804.39,165,100),v200,z50,tool0;
	MoveL Offs(P0,804.39,165,250),v300,z50,tool0;
	IA_OCUPADO:= 1;
  ENDPROC

PROC IB_E()
	MoveL Offs(P0,519.46,479.97,250),v300,z50,tool0;
	MoveL Offs(P0,519.46,479.97,100),v200,z50,tool0;
	MoveL Offs(P0,519.46,479.97,53.28),v30,fine,tool0;
		abre_garra;
	MoveL Offs(P0,519.46,479.97,100),v200,z50,tool0;
	MoveL Offs(P0,519.46,479.97,250),v300,z50,tool0;
	IB_OCUPADO:= 1;
  ENDPROC
  
  PROC PN_E()
  !Atenção os pontos não correspondem a realidade
	MoveL Offs(P0,1140,260,300),v300,z50,tool0;
	MoveL Offs(P0,1140,260,200),v200,z50,tool0;
	MoveL Offs(P0,1140,260,45),v30,fine,tool0;
		fecha_garra;
	MoveL Offs(P0,1140,260,300),v300,z50,tool0;
	PN_OCUPADO:= 1;
  ENDPROC
  
  PROC Verifica()
	SingArea\Wrist;
	MoveJ P_C, v500, z50, tool0;
	!CONNECT Barreira WITH INTERUPCAO;
	!ISignalDI DI10_14,1,Barreira;
	MoveL Offs(P_TESTE,0,15,100),v300, z10, tool0;
	MoveL Offs(P_TESTE,0,15,20),v200, z10, tool0;
	MoveL Offs(P_TESTE,0,15,0),v30, z10, tool0;
	!Set DO10_5;
	!	abre_garra;
	!Reset DO10_5;
	MoveL Offs(P_TESTE,0,10,100),v300, z10, tool0;
	abre_garra;
	MESA_OCUPADO:= 1;
	MoveJ P_C, v500, z10, tool0;
	IDelete Barreira;
  ENDPROC
  
  PROC Apos_Verifica()
  	SingArea\Wrist;
	MoveJ P_C, v500, z50, tool0;
	CONNECT Barreira WITH INTERUPCAO;
	ISignalDI DI10_14,1,Barreira;
	MoveL Offs(P_TESTE,0,0,200),v500, z10, tool0;
 	MoveL Offs(P_TESTE,-303.11,-3.88,196),v300, z10, tool0;
	MoveL Offs(P_TESTE,-303.11,-3.88,20),v200, z10, tool0;
	MoveL Offs(P_TESTE,-303.11,-3.88,-1.5), v20, fine, tool0;
		fecha_garra;
	MoveL Offs(P_TESTE,-303.11,-3.88,20),v200, z10, tool0;
	MoveL Offs(P_TESTE,-303.11,-3.88,196),v300, z10, tool0;
	MoveL Offs(P_TESTE,0,0,200),v300, z10, tool0;
  ENDPROC
  
  PROC Dep_OK()
	SingArea\Wrist;
	MoveJ P_C, v500, z10, tool0;
	IDelete Barreira;
	MoveL Offs(P1,715.31,320,250),v300,z10,tool0;
	MoveL Offs(P1,715.31,320,72),v300,fine,tool0;
		abre_garra;
	MoveL Offs(P1,715.31,320,250),v300,z10,tool0;
  ENDPROC

  PROC Dep_DEFEITO()
	SingArea\Wrist;
	MoveJ P_C, v500, z10, tool0;
	IDelete Barreira;
	MoveL Offs(P1,644.59,320,250),v300,z10,tool0;
	MoveL Offs(P1,644.59,320,72),v200,fine,tool0;
		abre_garra;
	MoveL Offs(P1,644.59,320,250),v300,z10,tool0;
  ENDPROC
  
  PROC GOOD()
      MoveJ [[-6.57,906.42,919.27],[0.024154,-0.003781,0.999689,0.005002],[1,-1,1,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]],v50,z50,tool0;
	  abre_garra;
  ENDPROC
  
  PROC BAD()
      MoveJ [[61.36,902.71,916.1],[0.023869,-0.003423,0.9997,0.004389],[0,-1,0,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]],v50,z50,tool0;
	  abre_garra;
  ENDPROC
  
  PROC ARMAZEM()
	MoveJ [[-209.64,841.02,899.59],[0.023471,-0.700582,-0.713098,0.011172],[1,0,2,0],[9E+09,9E+09,9E+09,9E+09,9E+09,9E+09]],v1000,z50,tool0;
	fecha_garra;
  ENDPROC
  
  TRAP INTERUPCAO
    StopMove;
    TPWrite "Parado por segurança";
    WaitDI DI10_14,0;
    StartMove;
  ENDTRAP
  
  PROC Buffer_E()
  	  	MoveL Offs(P_Buffer_E,0,0,200),v300, z10, tool0;
		MoveL Offs(P_Buffer_E,0,0,70),v200, z10, tool0;
		MoveL P_Buffer_E, v20, fine, tool0;
			abre_garra;
		MoveL Offs(P_Buffer_E,0,0,200),v200, z10, tool0;
		Buffer_OCUPADO:= Buffer_OCUPADO + 1;
  ENDPROC
  
  PROC Buffer_S()
  	  	MoveL Offs(P1,992.96,252.59,250),v300,z10,tool0;
		MoveL Offs(P1,992.96,252.59,65),v200,z10,tool0;
		MoveL Offs(P1,992.96,252.59,25.37),v30,fine,tool0;
			fecha_garra;
		MoveL Offs(P1,992.96,252.59,250),v300,z10,tool0;
		Buffer_OCUPADO:= Buffer_OCUPADO - 1;
  ENDPROC
  
  PROC abre_garra()
	ABERTA:= 1;
    Reset DOfecha;
    PulseDO\PLength:=0.2,DOabre;
    WaitTime 0.5;
  ENDPROC

  PROC fecha_garra()
	ABERTA:= 0;
    Reset DOabre;
    PulseDO\PLength:=0.2,DOfecha;
    WaitTime 0.5;
  ENDPROC
  
  

ENDMODULE