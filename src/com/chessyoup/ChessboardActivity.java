package com.chessyoup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.petero.droidfish.ChessBoardPlay;
import org.petero.droidfish.ChessController;
import org.petero.droidfish.ColorTheme;
import org.petero.droidfish.GUIInterface;
import org.petero.droidfish.GameMode;
import org.petero.droidfish.PGNOptions;
import org.petero.droidfish.Util.MaterialDiff;
import org.petero.droidfish.gamelogic.GameTree.Node;
import org.petero.droidfish.gamelogic.Move;
import org.petero.droidfish.gamelogic.PgnToken;
import org.petero.droidfish.gamelogic.Position;
import org.petero.droidfish.gamelogic.TextIO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

import com.chessyoup.connector.Connection;
import com.chessyoup.connector.ConnectionListener;
import com.chessyoup.connector.GenericDevice;
import com.chessyoup.connector.Message;
import com.chessyoup.server.RoomsManager;

public class ChessboardActivity extends Activity implements GUIInterface ,ConnectionListener {

	private boolean boardGestures = true;

	private ChessBoardPlay cb;

	private PgnScreenText gameTextListener;

	private ScrollView moveListScroll;

	private TextView moveListView;

	private ChessController chessCtrl;
	
	private Connection connection;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d("ChessboardActivity", "on create");
		setContentView(R.layout.chessboard);
		ColorTheme.instance().readColors(
				PreferenceManager.getDefaultSharedPreferences(this));
		PGNOptions pgnOptions = new PGNOptions();
		this.gameTextListener = new PgnScreenText(pgnOptions);
		this.chessCtrl = new ChessController(this, this.gameTextListener,
				pgnOptions);
		this.initUI();
		this.installListeners();
		
		Intent intent = getIntent();
		this.installListeners();

		if (intent.getExtras().getString("connected") != null
				&& intent.getExtras().getString("connected").equals("true")) {

			this.connection = RoomsManager
					.getManager()
					.getConnectionManager()
					.getConnection(
							intent.getStringExtra("remote_gcm_registration_id"));

			this.connection.setConnectionListener(this);			
			cb.flipped = true;
			this.chessCtrl.newGame(new GameMode(GameMode.TWO_PLAYERS_WHITE_REMOTE));
			this.chessCtrl.startGame();
			Toast.makeText(getApplicationContext(), "Game started", Toast.LENGTH_SHORT).show();
		} else {
			this.runConnectTask(intent);
		}
		
	}

	private void initUI() {
		cb = (ChessBoardPlay) findViewById(R.id.chessboard);
		cb.setFocusable(true);
		cb.requestFocus();
		cb.setClickable(true);
		cb.setPgnOptions(this.chessCtrl.getPgnOptions());

		final View chatView = LayoutInflater.from(this).inflate(R.layout.chat,null);
		final View gameView = LayoutInflater.from(this).inflate(R.layout.chessboard_game,null);
		this.moveListView = (TextView)gameView.findViewById(R.id.moveList);
		this.moveListScroll = (ScrollView)gameView.findViewById(R.id.moveListScroll);
		
		
		TabHost th = (TabHost) findViewById(android.R.id.tabhost);
		th.setup();

		TabSpec ts1 = th.newTabSpec("Chat");
		ts1.setIndicator(createTabView(this, "Chat")).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				return chatView;
			}
		});

		TabSpec ts2 = th.newTabSpec("Game");
		ts2.setIndicator(createTabView(this, "Game")).setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {
				return gameView;
			}
		});

		th.addTab(ts2);
		th.addTab(ts1);
		th.setCurrentTab(0);
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.border);
	    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(),bmp);
	    bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);	    
	    this.findViewById(R.id.chessboardLayout).setBackgroundDrawable(bitmapDrawable);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("ChessboardActivity", "on start");		
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("ChessboardActivity", "on spause");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("ChessboardActivity", "on resume");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d("ChessboardActivity", "on stop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("ChessboardActivity", "on destroy");
		this.runOnDisconectTask();
	}

	@Override
	public void setPosition(Position pos, String variantInfo,
			ArrayList<Move> variantMoves) {
		cb.setPosition(pos);
	}

	@Override
	public void setSelection(int sq) {
		cb.setSelection(sq);
		cb.userSelectedSquare = false;
	}
	
	@Override
	public void onConnected(Connection connection, boolean status) {
		this.connection = connection;
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				chessCtrl.newGame(new GameMode(GameMode.TWO_PLAYERS_BLACK_REMOTE));
				chessCtrl.startGame();
				Toast.makeText(getApplicationContext(), "Game started", Toast.LENGTH_SHORT).show();	
			}
		});
		
	}

	@Override
	public void messageReceived(Connection source,final Message message) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if( message.getHeader().get(GameMessage.GAME_COMMAND).equals(GameMessage.MOVE) ){
					ChessboardActivity.this.chessCtrl.makeRemoteMove(message.getBody());			
				}
			}
		});		
	}

	@Override
	public void setStatus(GameStatus s) {
		String str;
		switch (s.state) {
		case ALIVE:
			str = Integer.valueOf(s.moveNr).toString();
			if (s.white)
				str += ". " + getString(R.string.whites_move);
			else
				str += "... " + getString(R.string.blacks_move);
			if (s.ponder)
				str += " (" + getString(R.string.ponder) + ")";
			if (s.thinking)
				str += " (" + getString(R.string.thinking) + ")";
			if (s.analyzing)
				str += " (" + getString(R.string.analyzing) + ")";
			break;
		case WHITE_MATE:
			str = getString(R.string.white_mate);
			break;
		case BLACK_MATE:
			str = getString(R.string.black_mate);
			break;
		case WHITE_STALEMATE:
		case BLACK_STALEMATE:
			str = getString(R.string.stalemate);
			break;
		case DRAW_REP: {
			str = getString(R.string.draw_rep);
			if (s.drawInfo.length() > 0)
				str = str + " [" + s.drawInfo + "]";
			break;
		}
		case DRAW_50: {
			str = getString(R.string.draw_50);
			if (s.drawInfo.length() > 0)
				str = str + " [" + s.drawInfo + "]";
			break;
		}
		case DRAW_NO_MATE:
			str = getString(R.string.draw_no_mate);
			break;
		case DRAW_AGREE:
			str = getString(R.string.draw_agree);
			break;
		case RESIGN_WHITE:
			str = getString(R.string.resign_white);
			break;
		case RESIGN_BLACK:
			str = getString(R.string.resign_black);
			break;
		default:
			throw new RuntimeException();
		}

		// setStatusString(str);
	}

	@Override
	public void requestPromotePiece() {
		// TODO Auto-generated method stub

	}

	@Override
	public void runOnUIThread(Runnable runnable) {
		this.runOnUiThread(runnable);
	}

	@Override
	public void reportInvalidMove(Move m) {
		String msg = String.format("%s %s-%s",
				getString(R.string.invalid_move),
				TextIO.squareToString(m.from), TextIO.squareToString(m.to));
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void setRemainingTime(long wTime, long bTime, long nextUpdate) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMaterialDifferenceTitle(MaterialDiff diff) {
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext() {
		return this.getApplicationContext();
	}

	@Override
	public String whitePlayerName() {
		// TODO Auto-generated method stub
		return "white player";
	}

	@Override
	public String blackPlayerName() {
		// TODO Auto-generated method stub
		return "black player";
	}

	@Override
	public boolean discardVariations() {
		return false;
	}

	@Override
	public void moveListUpdated() {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				moveListView.setText(gameTextListener.getSpannableData());
				Layout layout = moveListView.getLayout();
				if (layout != null) {
					int currPos = gameTextListener.getCurrPos();
					int line = layout.getLineForOffset(currPos);
					int y = (int) ((line - 1.5) * moveListView.getLineHeight());
					moveListScroll.scrollTo(0, y);
				}			
			}
		});		
		
	}

	@Override
	public void setAnimMove(Position sourcePos, Move move, boolean forward) {
		cb.setAnimMove(sourcePos, move, forward);
	}

	@Override
	public void remoteMoveMade() {
		// Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		// v.vibrate(500);
	}
	
	private void runConnectTask(final Intent onCreateIntent) {
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				GenericDevice remoteDevice = new GenericDevice();
				remoteDevice.setAccount(onCreateIntent
						.getStringExtra("remote_account"));
				remoteDevice.setDeviceIdentifier(onCreateIntent
						.getStringExtra("remote_device_id"));
				remoteDevice.setRegistrationId(onCreateIntent
						.getStringExtra("remote_gcm_registration_id"));
				remoteDevice.setDevicePhoneNumber(onCreateIntent
						.getStringExtra("remote_phone_number"));

				RoomsManager.getManager().getConnectionManager()
						.connect(remoteDevice, ChessboardActivity.this);

				return null;
			}
		};

		task.execute();
	}
	
	private void installListeners() {

		final GestureDetector gd = new GestureDetector(this,
				new GestureDetector.SimpleOnGestureListener() {
					private float scrollX = 0;
					private float scrollY = 0;

					@Override
					public boolean onDown(MotionEvent e) {
						if (!boardGestures) {
							handleClick(e);
							return true;
						}
						scrollX = 0;
						scrollY = 0;
						return false;
					}

					@Override
					public boolean onScroll(MotionEvent e1, MotionEvent e2,
							float distanceX, float distanceY) {

						return true;
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						if (!boardGestures)
							return false;
						cb.cancelLongPress();
						handleClick(e);
						return true;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						if (!boardGestures)
							return false;
						if (e.getAction() == MotionEvent.ACTION_UP)
							handleClick(e);
						return true;
					}

					private final void handleClick(MotionEvent e) {
						if (true) {
							int sq = cb.eventToSquare(e);
							Move m = cb.mousePressed(sq);
							if (m != null){
								chessCtrl.makeHumanMove(m);
								sendMoveToRemote(TextIO.moveToUCIString(m));								
							}
						}
					}					
				});
		cb.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gd.onTouchEvent(event);
			}
		});

	}
	
	private void sendMoveToRemote(final String move) {
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				try {					
					connection.sendMessage(new GameMessage(GameMessage.MOVE, move));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return null;
			}
		};

		task.execute();						
	}
	
	class GameMessage implements Message{
		
		public static final String GAME_COMMAND = "cmd";
		
		public static final String CHAT = "chat";
		
		public static final String MOVE = "move";
		
		private Map<String, String> header;
		
		private String body;
		
		GameMessage(String type,String body){
			header = new HashMap<String, String>();
			header.put("GAME_COMMAND", type);
			this.body = body;
		}
		
		@Override
		public String getBody() {
			return this.body;
		}

		@Override
		public Map<String, String> getHeader() {
			return this.header;
		}		
	}
	
	/**
	 * PngTokenReceiver implementation that renders PGN data for screen display.
	 */
	static class PgnScreenText implements PgnToken.PgnTokenReceiver {
		private SpannableStringBuilder sb = new SpannableStringBuilder();
		private int prevType = PgnToken.EOF;
		int nestLevel = 0;
		boolean col0 = true;
		Node currNode = null;
		final static int indentStep = 15;
		int currPos = 0, endPos = 0;
		boolean upToDate = false;
		PGNOptions options;

		private static class NodeInfo {
			int l0, l1;

			NodeInfo(int ls, int le) {
				l0 = ls;
				l1 = le;
			}
		}

		HashMap<Node, NodeInfo> nodeToCharPos;

		PgnScreenText(PGNOptions options) {
			nodeToCharPos = new HashMap<Node, NodeInfo>();
			this.options = options;
		}

		public final SpannableStringBuilder getSpannableData() {
			return sb;
		}

		public final int getCurrPos() {
			return currPos;
		}

		public boolean isUpToDate() {
			return upToDate;
		}

		int paraStart = 0;
		int paraIndent = 0;
		boolean paraBold = false;

		private final void newLine() {
			newLine(false);
		}

		private final void newLine(boolean eof) {
			if (!col0) {
				if (paraIndent > 0) {
					int paraEnd = sb.length();
					int indent = paraIndent * indentStep;
					sb.setSpan(new LeadingMarginSpan.Standard(indent),
							paraStart, paraEnd,
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (paraBold) {
					int paraEnd = sb.length();
					sb.setSpan(new StyleSpan(Typeface.BOLD), paraStart,
							paraEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				if (!eof)
					sb.append('\n');
				paraStart = sb.length();
				paraIndent = nestLevel;
				paraBold = false;
			}
			col0 = true;
		}

		boolean pendingNewLine = false;

		/** Makes moves in the move list clickable. */
		private final static class MoveLink extends ClickableSpan {
			private Node node;

			MoveLink(Node n) {
				node = n;
			}

			@Override
			public void onClick(View widget) {
				// if (ctrl != null)
				// ctrl.goNode(node);
			}

			@Override
			public void updateDrawState(TextPaint ds) {
			}
		}

		public void processToken(Node node, int type, String token) {
			if ((prevType == PgnToken.RIGHT_BRACKET)
					&& (type != PgnToken.LEFT_BRACKET)) {
				if (options.view.headers) {
					col0 = false;
					newLine();
				} else {
					sb.clear();
					paraBold = false;
				}
			}
			if (pendingNewLine) {
				if (type != PgnToken.RIGHT_PAREN) {
					newLine();
					pendingNewLine = false;
				}
			}
			switch (type) {
			case PgnToken.STRING:
				sb.append(" \"");
				sb.append(token);
				sb.append('"');
				break;
			case PgnToken.INTEGER:
				if ((prevType != PgnToken.LEFT_PAREN)
						&& (prevType != PgnToken.RIGHT_BRACKET) && !col0)
					sb.append(' ');
				sb.append(token);
				col0 = false;
				break;
			case PgnToken.PERIOD:
				sb.append('.');
				col0 = false;
				break;
			case PgnToken.ASTERISK:
				sb.append(" *");
				col0 = false;
				break;
			case PgnToken.LEFT_BRACKET:
				sb.append('[');
				col0 = false;
				break;
			case PgnToken.RIGHT_BRACKET:
				sb.append("]\n");
				col0 = false;
				break;
			case PgnToken.LEFT_PAREN:
				nestLevel++;
				if (col0)
					paraIndent++;
				newLine();
				sb.append('(');
				col0 = false;
				break;
			case PgnToken.RIGHT_PAREN:
				sb.append(')');
				nestLevel--;
				pendingNewLine = true;
				break;
			case PgnToken.NAG:
				sb.append(Node.nagStr(Integer.parseInt(token)));
				col0 = false;
				break;
			case PgnToken.SYMBOL: {
				if ((prevType != PgnToken.RIGHT_BRACKET)
						&& (prevType != PgnToken.LEFT_BRACKET) && !col0)
					sb.append(' ');
				int l0 = sb.length();
				sb.append(token);
				int l1 = sb.length();
				nodeToCharPos.put(node, new NodeInfo(l0, l1));
				sb.setSpan(new MoveLink(node), l0, l1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				if (endPos < l0)
					endPos = l0;
				col0 = false;
				if (nestLevel == 0)
					paraBold = true;
				break;
			}
			case PgnToken.COMMENT:
				if (prevType == PgnToken.RIGHT_BRACKET) {
				} else if (nestLevel == 0) {
					nestLevel++;
					newLine();
					nestLevel--;
				} else {
					if ((prevType != PgnToken.LEFT_PAREN) && !col0) {
						sb.append(' ');
					}
				}
				int l0 = sb.length();
				sb.append(token.replaceAll("[ \t\r\n]+", " ").trim());
				int l1 = sb.length();
				int color = ColorTheme.instance().getColor(
						ColorTheme.PGN_COMMENT);
				sb.setSpan(new ForegroundColorSpan(color), l0, l1,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				col0 = false;
				if (nestLevel == 0)
					newLine();
				break;
			case PgnToken.EOF:
				newLine(true);
				upToDate = true;
				break;
			}
			prevType = type;
		}

		@Override
		public void clear() {
			sb.clear();
			prevType = PgnToken.EOF;
			nestLevel = 0;
			col0 = true;
			currNode = null;
			currPos = 0;
			endPos = 0;
			nodeToCharPos.clear();
			paraStart = 0;
			paraIndent = 0;
			paraBold = false;
			pendingNewLine = false;

			upToDate = false;
		}

		BackgroundColorSpan bgSpan = new BackgroundColorSpan(0xff888888);

		@Override
		public void setCurrent(Node node) {
			sb.removeSpan(bgSpan);
			NodeInfo ni = nodeToCharPos.get(node);
			if ((ni == null) && (node != null) && (node.getParent() != null))
				ni = nodeToCharPos.get(node.getParent());
			if (ni != null) {
				int color = ColorTheme.instance().getColor(
						ColorTheme.CURRENT_MOVE);
				bgSpan = new BackgroundColorSpan(color);
				sb.setSpan(bgSpan, ni.l0, ni.l1,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				currPos = ni.l0;
			} else {
				currPos = 0;
			}
			currNode = node;
		}
	}

	@Override
	public void onDisconnected(Connection source) {
		this.runOnDisconectTask();
		finish();
	}
	
	private void runOnDisconectTask() {

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {

				if( ChessboardActivity.this.connection != null ){
					RoomsManager.getManager().getConnectionManager().closeConnection(ChessboardActivity.this.connection);
				}

				return null;
			}
		};

		task.execute();
	}
	
	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}
