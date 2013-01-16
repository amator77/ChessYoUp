package com.chessyoup;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.petero.droidfish.ChessBoardPlay;
import org.petero.droidfish.ChessController;
import org.petero.droidfish.ColorTheme;
import org.petero.droidfish.GUIInterface;
import org.petero.droidfish.GameMode;
import org.petero.droidfish.PGNOptions;
import org.petero.droidfish.Util.MaterialDiff;
import org.petero.droidfish.gamelogic.Game.GameState;
import org.petero.droidfish.gamelogic.GameTree.Node;
import org.petero.droidfish.gamelogic.Move;
import org.petero.droidfish.gamelogic.PgnToken;
import org.petero.droidfish.gamelogic.Position;
import org.petero.droidfish.gamelogic.TextIO;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.chessyoup.transport.xmpp.XMPPGameController;
import com.chessyoup.transport.xmpp.XMPPGameListener;

public class XMPPChessBoardActivity extends Activity implements GUIInterface,
		XMPPGameListener {

	private boolean boardGestures = true;

	private ChessBoardPlay cb;

	private PgnScreenText gameTextListener;

	private ScrollView moveListScroll;

	private TextView moveListView;

	private TextView chatDisplay;

	private Button chatSendMessageButton;

	private EditText chatEditText;

	private DateFormat dateFormat;

	private ChessController chessCtrl;

	private String ownerJID;

	private String remoteJID;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d("ChessboardActivity", "on create");
		setContentView(R.layout.chessboard);
		dateFormat = new SimpleDateFormat("EEEE, kk:mm", Locale.getDefault());
		ColorTheme.instance().readColors(
				PreferenceManager.getDefaultSharedPreferences(this));
		PGNOptions pgnOptions = new PGNOptions();
		this.gameTextListener = new PgnScreenText(pgnOptions);
		this.chessCtrl = new ChessController(this, this.gameTextListener,
				pgnOptions);
		Intent intent = getIntent();
		this.ownerJID = intent.getExtras().getString("ownerJID");
		this.remoteJID = intent.getExtras().getString("remoteJID");

		this.initUI();
		this.installListeners();

		Log.d("XMPPChessBoardActivity", "ownerId :" + this.ownerJID);
		Log.d("XMPPChessBoardActivity", "remoteId :" + this.remoteJID);

		XMPPGameController.getController().setGameListener(this);

		if (intent.getExtras().getString("autostart") != null
				&& intent.getExtras().getString("autostart").equals("true")) {
			String whitePlayer = intent.getExtras().getString("color")
					.equals("white") ? this.ownerJID : remoteJID;
			String blackPlayer = intent.getExtras().getString("color")
					.equals("black") ? this.remoteJID : ownerJID;

			XMPPGameController.getController().sendGameStartAcceptedCommand(
					remoteJID, whitePlayer, blackPlayer);
			this.startGame(whitePlayer, blackPlayer);
		} else {

			XMPPGameController.getController().sendGameStartRequestCommand(
					this.remoteJID, this.ownerJID, this.remoteJID);
			Toast.makeText(getApplicationContext(), "Send game request! ",
					Toast.LENGTH_LONG).show();
		}
	}

	private void initUI() {
		final View chatView = LayoutInflater.from(this).inflate(R.layout.chat,
				null);
		final View gameView = LayoutInflater.from(this).inflate(
				R.layout.chessboard_game, null);
		this.moveListView = (TextView) gameView.findViewById(R.id.moveList);
		this.moveListScroll = (ScrollView) gameView
				.findViewById(R.id.moveListScroll);
		this.chatDisplay = (TextView) chatView.findViewById(R.id.chatDisplay);
		this.chatEditText = (EditText) chatView.findViewById(R.id.editChatText);
		this.chatSendMessageButton = (Button) chatView
				.findViewById(R.id.sendChatButton);

		cb = (ChessBoardPlay) findViewById(R.id.chessboard);
		cb.setFocusable(true);
		cb.requestFocus();
		cb.setClickable(true);
		cb.setPgnOptions(this.chessCtrl.getPgnOptions());

		TabHost th = (TabHost) findViewById(android.R.id.tabhost);
		th.setup();

		TabSpec ts1 = th.newTabSpec("Chat");
		ts1.setIndicator(createTabView(this, "Chat")).setContent(
				new TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return chatView;
					}
				});

		TabSpec ts2 = th.newTabSpec("Game");
		ts2.setIndicator(createTabView(this, "Game")).setContent(
				new TabContentFactory() {

					@Override
					public View createTabContent(String tag) {
						return gameView;
					}
				});

		th.addTab(ts2);
		th.addTab(ts1);
		th.setCurrentTab(0);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.border);
		BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bmp);
		bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT,
				Shader.TileMode.REPEAT);
		this.findViewById(R.id.chessboardLayout).setBackgroundDrawable(
				bitmapDrawable);
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
		XMPPGameController.getController().setGameListener(null);
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
							if (m != null) {
								if (chessCtrl.humansTurn()) {
									chessCtrl.makeHumanMove(m);
									sendMoveToRemote(TextIO.moveToUCIString(m));
								}
							}
						}
					}
				});
		cb.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gd.onTouchEvent(event);
			}
		});

		chatSendMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("GCMChatActivity", "Send message request.");
				runSendMessageTask(chatEditText.getEditableText().toString());
				chatEditText.setText("");
			}
		});

		chatEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null
						&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
						&& (event.getAction() == KeyEvent.ACTION_UP)) {
					InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					Log.d("key event", event.toString());

					runSendMessageTask(chatEditText.getEditableText()
							.toString());
					chatEditText.setText("");

					in.hideSoftInputFromWindow(v.getApplicationWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

					return true;
				}
				return false;
			}
		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			
			if( this.chessCtrl.getGame().getGameState() == GameState.ALIVE ){
				AlertDialog.Builder db = new AlertDialog.Builder(this);
				db.setTitle("Warning");
				String actions[] = new String[2];
				actions[0] = "Resign!";
				actions[1] = "Cancel";
				db.setItems(actions, new DialogInterface.OnClickListener() {
		
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							runResignTask();
							finish();
							break;
						case 1:
							break;
						default:
		
							break;
						}
					}
				});
		
				AlertDialog ad = db.create();
				ad.setCancelable(true);
				ad.setCanceledOnTouchOutside(false);
				ad.show();

				return true;
			}
			else{
				finish();
				return false;
			}
		} else {
			finish();
			return false;
		}
	}
	
	private void runResignTask() {
		this.chessCtrl.resignGame();
		
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				XMPPGameController.getController().sendGameResignCommand(remoteJID);
				return null;
			}
		};

		task.execute();		
	}
	
	private void runSendMessageTask(final String message) {
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				XMPPGameController.getController().sendGameChatCommand(
						remoteJID, message);
				addMessage(ownerJID, message);
				return null;
			}
		};

		task.execute();
	}

	private void sendMoveToRemote(final String move) {

		Log.d("ChessBoardActivity", "Send move to remote :" + move);

		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				XMPPGameController.getController().sendGameMoveCommand(
						remoteJID, move);
				return null;
			}
		};

		task.execute();
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

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context)
				.inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	private void addMessage(final String source, final String text) {
		this.runOnUIThread(new Runnable() {

			@Override
			public void run() {
				StringBuilder sb = new StringBuilder();
				sb.append("<span style=\"color:red\">")
						.append(dateFormat.format(new Date()))
						.append(" </span>");
				sb.append(source != null ? source : "null").append(" : ");
				sb.append(text != null ? text : "null").append("<br/>");
				Spanned styledText = Html.fromHtml(sb.toString());
				chatDisplay.append(styledText);
			}
		});
	}

	@Override
	public void startGameRequestReceived(String jabberID, String whitePlayer,
			String blackPlayer) {
		XMPPGameController.getController().sendGameStartRequestCommand(
				this.remoteJID, whitePlayer, blackPlayer);

		this.startGame(whitePlayer, blackPlayer);
	}

	@Override
	public void gameStarted(final String whitePlayerJID,
			final String blackPlayerJID) {
		this.startGame(whitePlayerJID, whitePlayerJID);
	}

	private void startGame(final String whitePlayer, String blackPlayer) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (!whitePlayer.equals(ownerJID)) {
					cb.flipped = false;
					chessCtrl.newGame(new GameMode(
							GameMode.TWO_PLAYERS_BLACK_REMOTE));
				} else {
					cb.flipped = true;
					chessCtrl.newGame(new GameMode(
							GameMode.TWO_PLAYERS_WHITE_REMOTE));
				}

				chessCtrl.startGame();
				Toast.makeText(getApplicationContext(), "Game started",
						Toast.LENGTH_SHORT).show();

			}
		});
	}

	@Override
	public void moveReceived(String jabberID, final String move) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				XMPPChessBoardActivity.this.chessCtrl.makeRemoteMove(move);
			}
		});
	}

	@Override
	public void chatReceived(final String jabberID, final String text) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				addMessage(jabberID, text);
			}
		});
	}

	@Override
	public void drawRequestReceived(String jabberID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawAccepted(String jabberID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resignReceived(String jabberID) {
		this.chessCtrl.makeRemoteMove("resign");
		Toast.makeText(getApplicationContext(), jabberID+" resigned!",
				Toast.LENGTH_LONG).show();
	}

	@Override
	public void abortRequestReceived(String jabberID) {
		// TODO Auto-generated method stub

	}

	@Override
	public void abortRequestAccepted(String jabberID) {
		// TODO Auto-generated method stub

	}
}
