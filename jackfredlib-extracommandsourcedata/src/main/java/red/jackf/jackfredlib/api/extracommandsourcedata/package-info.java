/**
 * <p>Toolkit for adding arbitrary data to Minecraft's {@link net.minecraft.commands.CommandSourceStack}. This allows for
 * some features such as repeatable arguments.</p>
 * <h2>Getting Started</h2>
 * To get started, create a new class which will hold your data:
 * <pre>
 * {@code
 *  public class MyData implements ExtraSourceData<MyData> {
 *      private final List<Integer> ints = new ArrayList<>();
 *
 *      public void addInt(int value) {
 *          this.ints.add(value);
 *      }
 *
 *      public List<Integer> getInts() {
 *          return this.ints;
 *      }
 *
 *      @Override
 *      public MyData copy() {
 *          var copy = new MyData();
 *          copy.ints.addAll(this.ints);
 *          return copy;
 *      }
 *  }
 * }
 * </pre>
 * Then, create a new {@link red.jackf.jackfredlib.api.extracommandsourcedata.ExtraSourceData.Definition}:
 * <pre>
 * {@code
 *  public static final ExtraSourceData.Definition<MyData> MYDATA = new ExtraSourceData.Definition<>(
 *      new ResourceLocation("mymod", "intcollector"),
 *      MyData.class,
 *      MyData::new
 *  );
 * }
 * </pre>
 * In your command builder, use the <b>with</b> methods in {@link red.jackf.jackfredlib.api.extracommandsourcedata.ESD}
 * in a command node's {@link com.mojang.brigadier.builder.ArgumentBuilder#redirect(com.mojang.brigadier.tree.CommandNode, com.mojang.brigadier.SingleRedirectModifier)}
 * or {@link com.mojang.brigadier.builder.ArgumentBuilder#fork(com.mojang.brigadier.tree.CommandNode, com.mojang.brigadier.RedirectModifier)}
 * methods:
 * <pre>
 * {@code
 *  var root = dispatcher.register(literal("intCollector"));
 *
 *  dispatcher.register(literal("intCollector").then(
 *      argument("int", IntegerArgumentType.integer())
 *          .redirect(root, ctx -> ESD.withCustom(ctx, MYDATA, myData -> {
 *              myData.addInt(ctx.getArgument("int", Integer.class));
 *          }))
 *      ).then(
 *          literal("show").executes(ctx -> {
 *              ctx.getSource().getPlayerOrException()
 *                  .sendSystemMessage(Component.literal("collected: " + ESD.getCustom(ctx, MYDATA).getInts()));
 *              return 0;
 *      })));
 * }
 * </pre>
 * <h2>Notes</h2>
 * If you put an <code>executes</code> function on the same node with a redirect (such as on the <code>int</code> argument node
 * in the example above), then a command finishing on that execute <b>will not run the redirect modifier</b>, which could
 * result in some missed data. This is the purpose of using a <i>finishing</i> literal ("show" in the example). You can also
 * collect this missed data by manually adding a {@link com.mojang.brigadier.context.CommandContext#getArgument(java.lang.String, java.lang.Class)}
 * call at the start of your <code>execute</code> method.
 */
package red.jackf.jackfredlib.api.extracommandsourcedata;